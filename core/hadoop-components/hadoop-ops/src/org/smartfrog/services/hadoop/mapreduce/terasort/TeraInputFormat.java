/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartfrog.services.hadoop.mapreduce.terasort;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.IndexedSortable;
import org.apache.hadoop.util.QuickSort;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An input format that reads the first 10 characters of each line as the key and the rest of the line as the value.
 * Both key and value are represented as Text.
 */
@SuppressWarnings({"deprecation"})
public class TeraInputFormat extends FileInputFormat<Text, Text> {
    private static final Log LOG = LogFactory.getLog(TeraInputFormat.class);

    private static JobConf lastConf = null;
    private static InputSplit[] lastResult = null;

    static class TextSampler implements IndexedSortable {
        private ArrayList<Text> records = new ArrayList<Text>();

        @Override
        public int compare(int i, int j) {
            Text left = records.get(i);
            Text right = records.get(j);
            return left.compareTo(right);
        }

        @Override
        public void swap(int i, int j) {
            Text left = records.get(i);
            Text right = records.get(j);
            records.set(j, left);
            records.set(i, right);
        }

        public void addKey(Text key) {
            records.add(new Text(key));
        }

        /**
         * Find the split points for a given sample. The sample keys are sorted and down sampled to find even split points
         * for the partitions. The returned keys should be the start of their respective partitions.
         *
         * @param numPartitions the desired number of partitions
         * @return an array of size numPartitions - 1 that holds the split points
         */
        Text[] createPartitions(int numPartitions) {
            int numRecords = records.size();
            LOG.info("Making " + numPartitions + " from " + numRecords +
                    " records");
            if (numPartitions > numRecords) {
                throw new IllegalArgumentException
                        ("Requested more partitions than input keys (" + numPartitions +
                                " > " + numRecords + ")");
            }
            new QuickSort().sort(this, 0, records.size());
            float stepSize = numRecords / (float) numPartitions;
            LOG.info("Step size is " + stepSize);
            Text[] result = new Text[numPartitions - 1];
            for (int i = 1; i < numPartitions; ++i) {
                result[i - 1] = records.get(Math.round(stepSize * i));
            }
            return result;
        }
    }

    /**
     * Use the input splits to take samples of the input and generate sample keys. By default reads 100,000 keys from 10
     * locations in the input, sorts them and picks N-1 keys to generate N equally sized partitions.
     *
     * @param conf     the job to sample
     * @param partFile where to write the output file to
     * @throws IOException if something goes wrong
     */
    public static void writePartitionFile(JobConf conf,
                                          Path partFile) throws IOException {
        TeraInputFormat inFormat = new TeraInputFormat();
        TextSampler sampler = new TextSampler();
        Text key = new Text();
        Text value = new Text();
        int partitions = conf.getNumReduceTasks();
        long sampleSize = conf.getLong(TeraConstants.SAMPLE_SIZE, 100000);
        InputSplit[] splits = inFormat.getSplits(conf, conf.getNumMapTasks());
        int samples = Math.min(10, splits.length);
        long recordsPerSample = sampleSize / samples;
        int sampleStep = splits.length / samples;
        long records = 0;
        // take N samples from different parts of the input
        for (int i = 0; i < samples; ++i) {
            RecordReader<Text, Text> reader =
                    inFormat.getRecordReader(splits[sampleStep * i], conf, null);
            while (reader.next(key, value)) {
                sampler.addKey(key);
                records += 1;
                if ((i + 1) * recordsPerSample <= records) {
                    break;
                }
            }
        }
        FileSystem outFs = partFile.getFileSystem(conf);
        if (outFs.exists(partFile)) {
            outFs.delete(partFile, false);
        }
        SequenceFile.Writer writer =
                SequenceFile.createWriter(outFs, conf, partFile, Text.class,
                        NullWritable.class);
        NullWritable nullValue = NullWritable.get();
        for (Text split : sampler.createPartitions(partitions)) {
            writer.append(split, nullValue);
        }
        writer.close();
    }

    public static class TeraRecordReader implements RecordReader<Text, Text> {
        private LineRecordReader in;
        private LongWritable junk = new LongWritable();
        private Text line = new Text();
        private static int KEY_LENGTH = 10;

        public TeraRecordReader(Configuration job,
                                FileSplit split) throws IOException {
            in = new LineRecordReader(job, split);
        }

        @Override
        public void close() throws IOException {
            in.close();
        }

        @Override
        public Text createKey() {
            return new Text();
        }

        @Override
        public Text createValue() {
            return new Text();
        }

        @Override
        public long getPos() throws IOException {
            return in.getPos();
        }

        @Override
        public float getProgress() throws IOException {
            return in.getProgress();
        }

        @Override
        public boolean next(Text key, Text value) throws IOException {
            if (in.next(junk, line)) {
                if (line.getLength() < KEY_LENGTH) {
                    key.set(line);
                    value.clear();
                } else {
                    byte[] bytes = line.getBytes();
                    key.set(bytes, 0, KEY_LENGTH);
                    value.set(bytes, KEY_LENGTH, line.getLength() - KEY_LENGTH);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public RecordReader<Text, Text>
    getRecordReader(InputSplit split,
                    JobConf job,
                    Reporter reporter) throws IOException {
        return new TeraRecordReader(job, (FileSplit) split);
    }

    @Override
    public InputSplit[] getSplits(JobConf conf, int splits) throws IOException {
        if (conf == lastConf) {
            return lastResult;
        }
        lastConf = conf;
        lastResult = super.getSplits(conf, splits);
        return lastResult;
    }
}
