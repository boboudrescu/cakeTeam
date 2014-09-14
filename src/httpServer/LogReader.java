/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benchmade
 */
public class LogReader {

    public List readFromFile() {
        List<String> list = new ArrayList();
        Scanner input = null;

        try {
            input = new Scanner(new File("chatLog.txt"));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (input.hasNextLine()) {
            Scanner scan = new Scanner(input.nextLine());

            list.add(scan.nextLine());

        }
        input.close();
        return list;
    }
}
