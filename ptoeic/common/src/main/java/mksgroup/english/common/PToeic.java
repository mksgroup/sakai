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
     * Usage: ptoeic <input.txt> <out-excel-path>.
     * @param args
     */
    public static void main(String[] args) {
        String inputTextFile = args[0];
        String outExcelPath = args[1];
        String text;

        try {
            text = FileUtil.getContent(new File(inputTextFile), "utf-8");
            ToeicData toeicData = new ToeicData(text);

            AppUtility.write(toeicData, outExcelPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
