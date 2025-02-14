/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * This task takes a file and turns it into a URL, which it then assigns to a property. This is a way of getting file:
 * URLs into an inline SmartFrog deployment descriptor.
 *
 * nested filesets are supported; if present, these are turned into the url with the given separator between them
 * (default = " ").
 *
 * This file is deprecated in Ant1.7, because we donated our code and tests to them -it is now built in.
 *
 * @ant.task category="SmartFrog" name="sf-tourl"
 */

public class ToUrlTask extends Task {

    /**
     * name of the property to set
     */
    private String property;

    /**
     * name of a file to turn into a URL
     */
    private File file;

    /**
     * separator char
     */
    private String separator = " ";

    /**
     * filesets of nested files to add to this url
     */
    private List<FileSet> filesets = new LinkedList<FileSet>();

    /**
     * paths to add
     */
    private List<Path> paths = new LinkedList<Path>();

    /**
     * validation flag
     */
    private boolean validate = true;

    /**
     * error message
     */
    public static final String ERROR_MISSING_FILE = "A source file is missing :";
    public static final String ERROR_NO_PROPERTY = "No property defined";
    public static final String ERROR_NO_FILES = "No files defined";

    /**
     * set the name of a property to fill with the URL
     *
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * the name of a file to be converted into a URL
     *
     * @param file a file to turn into a URL
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * a fileset of jar files to include in the URL, each separated by the separator
     *
     * @param fileset files to add
     */
    public void addFileSet(FileSet fileset) {
        filesets.add(fileset);
    }

    /**
     * set the separator for the multi-url option.
     *
     * @param separator separator between urls
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * set this flag to trigger validation that every named file exists. Optional: default=true
     *
     * @param validate flat to trigger check for files
     */
    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    /**
     * add a path to the URL. All elements in the path will be converted to individual URL entries
     *
     * @param path path to convert to urls
     */
    public void addPath(Path path) {
        paths.add(path);
    }

    /**
     * convert the filesets to urls.
     *
     * @return null for no files
     */
    private String filesetsToURL() {
        if (filesets.isEmpty()) {
            return "";
        }
        int count = 0;
        StringBuffer urls = new StringBuffer();
        for (FileSet set : filesets) {
            DirectoryScanner scanner = set.getDirectoryScanner(getProject());
            String[] files = scanner.getIncludedFiles();
            for (String file1 : files) {
                File f = new File(scanner.getBasedir(), file1);
                validateFile(f);
                String asUrl = toURL(f);
                urls.append(asUrl);
                log(asUrl, Project.MSG_DEBUG);
                urls.append(separator);
                count++;
            }
        }
        //at this point there is one trailing space to remove, if the list is not empty.
        return stripTrailingSeparator(urls, count);
    }

    /**
     * convert the string buffer to a string, potentially stripping out any trailing separator
     *
     * @param urls  URL buffer
     * @param count number of URL entries
     * @return trimmed string, or empty string
     */
    private String stripTrailingSeparator(StringBuffer urls,
                                          int count) {
        if (count > 0) {
            urls.delete(urls.length() - separator.length(), urls.length());
            return new String(urls);
        } else {
            return "";
        }
    }


    /**
     * convert all paths to URLs
     *
     * @return the paths as a separated list of URLs
     */
    private String pathsToURL() {
        if (paths.isEmpty()) {
            return "";
        }
        int count = 0;
        StringBuffer urls = new StringBuffer();
        ListIterator<?> list = paths.listIterator();
        while (list.hasNext()) {
            Path path = (Path) list.next();
            String[] elements = path.list();
            for (String element : elements) {
                File f = new File(element);
                validateFile(f);
                String asUrl = toURL(f);
                urls.append(asUrl);
                log(asUrl, Project.MSG_DEBUG);
                urls.append(separator);
                count++;
            }
        }
        //at this point there is one trailing space to remove, if the list is not empty.
        return stripTrailingSeparator(urls, count);
    }

    /**
     * verify that the file exists, if {@link #validate} is set
     *
     * @param fileToCheck file that may need to exist
     * @throws BuildException with text beginning {@link #ERROR_MISSING_FILE}
     */
    private void validateFile(File fileToCheck) {
        if (validate && !fileToCheck.exists()) {
            throw new BuildException(ERROR_MISSING_FILE + fileToCheck.toString());
        }
    }

    /**
     * Create the url
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build
     */
    public void execute() throws BuildException {
        validate();
        //now exit here if the property is already set
        if (getProject().getProperty(property) != null) {
            return;
        }
        String url;
        String filesetURL = filesetsToURL();
        if (file != null) {
            validateFile(file);
            url = toURL(file);
            //and add any files if also defined
            if (filesetURL.length() > 0) {
                url = url + separator + filesetURL;
            }
        } else {
            url = filesetURL;
        }
        //add path URLs
        String pathURL = pathsToURL();
        if (pathURL.length() > 0) {
            if (url.length() > 0) {
                url = url + separator + pathURL;
            } else {
                url = pathURL;
            }
        }
        log("Setting " + property + " to URL " + url, Project.MSG_VERBOSE);
        getProject().setNewProperty(property, url);
    }

    private void validate() {
        //validation
        if (property == null) {
            throw new BuildException(ERROR_NO_PROPERTY);
        }
        if (file == null && filesets.isEmpty() && paths.isEmpty()) {
            throw new BuildException(ERROR_NO_FILES);
        }
    }

    /**
     * convert a file to a URL;
     *
     * @param fileToConvert file to turn to a URL
     * @return the file converted to a URL
     * @throws BuildException if the file would not convert
     */
    private String toURL(File fileToConvert) {
        String url;
        try {
            //create the URL
            url = fileToConvert.toURI().toURL().toExternalForm();
            //set the property
        } catch (MalformedURLException e) {
            throw new BuildException("Could not convert " + fileToConvert, e);
        }
        return url;
    }

}
