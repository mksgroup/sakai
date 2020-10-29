/**
 * Copyright 2018, MKS GROUP.
 */
package mksgroup.english.common;

import java.io.File;
import java.io.IOException;

import mksgroup.java.common.FileUtil;

/**
 * @author Le Ngoc Thach
 *
 */
public class PToeic {

    /**
     * Usage: ptoeic <path>.
     * @param args
     */
    public static void main(String[] args) {
        String folderPath = args[0];
        

        ToeicParser parser = new ToeicParser(folderPath);
    }

}
