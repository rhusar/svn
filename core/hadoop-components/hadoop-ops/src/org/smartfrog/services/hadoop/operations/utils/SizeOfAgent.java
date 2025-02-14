package org.smartfrog.services.hadoop.operations.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.instrument.Instrumentation;

/**
 * Uses the Instrumentation API to determine the size of an entity.
 * <p/>
 * <p/>
 * Needs to be built with a manifest that says Agent-Class or Premain-Class of org.smartfrog.services.hadoop.common.SizeOfAgent
 */
public class SizeOfAgent {

    /**
     * This is the interface we only get if started as an agent
     */
    private static Instrumentation instrumentation;
    private static final Log LOG = LogFactory.getLog(SizeOfAgent.class);


    /**
     * Calculate size of an item and print it
     *
     * @param name     object name object name: null to work it out
     * @param instance instance to measure the size of
     */
    private static void printSizeof(String name, Object instance) {
        long size = SizeOfAgent.sizeOf(instance);
        printSizeof(
                name == null ?
                        instance.getClass().toString()
                        : name,
                size);
    }

    /**
     * Print out the size of an item, iff the size > 0
     *
     * @param name name to print out
     * @param size size to print
     */
    private static void printSizeof(String name, long size) {
        if (size >= 0) {
            LOG.info("sizeof(" + name + ") = " + size);
        } else {
            LOG.info("sizeof(" + name + ") not known: sizeof agent is not installed");
        }
    }

    /**
     * Do we have the sizeof operator?
     *
     * @return true iff sizeof is working
     */
    public static boolean hasSizeof() {
        return instrumentation != null;
    }

    /**
     * Determine the size of an instance. Only works if we are running with instrumentation
     *
     * @param instance object instance to look at
     * @return the size, or -1 if we don't know
     */
    public static long sizeOf(Object instance) {
        if (!hasSizeof()) {
            return -1;
        }
        return instrumentation.getObjectSize(instance);
    }

    /**
     * This is called on startup to measure the size
     *
     * @param agentArgs any agent arguments
     * @param inst      the instrumetnation interface
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
    }

    /**
     * This is called if (somehow) the class has been dynamically registered as an agent
     *
     * @param agentArgs any agent arguments
     * @param inst      the instrumetnation interface
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
    }


    /**
     * And here is a main file to print off the sizes
     *
     * @param args the arguments
     *
     * @throws Throwable on a failure
     */
/*    public static void main(String[] args) throws Throwable {
        printSizeof("Object", new Object());
        for (int i = 0; i <= (3*8); i++) {
            printSizeof("Object[" + i + "]", new Object[i]);
        }
        printSizeof("String", "");
        printSizeof("BlockInfo", SizeofNamenode.sizeOfBlockInfo());
        printSizeof("INode", SizeofNamenode.sizeOfINodeFile());
        printSizeof("INodeDirectory", SizeofNamenode.sizeOfINodeDirectory());
        printSizeof("INodeDirectorywithQuota", SizeofNamenode.sizeOfINodeDirectoryWithQuota());
        printSizeof("DatanodeDescriptor", new DatanodeDescriptor());
        for (int i=0; i<8 ; i++) {
            printSizeof("BlockInfo replicas="+i, SizeofNamenode.sizeOfBlockInfo(i));
        }
    }*/

}
