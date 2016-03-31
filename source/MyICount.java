import BIT.highBIT.*;
import java.io.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;


public class MyICount {
    private static PrintStream out = null;
    private static int i_count = 0, b_count = 0, m_count = 0;
    
    public static void main(String argv[]) {
        
        ClassInfo ci = new ClassInfo("IntFactorization.class");
		
        for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements(); ) {
            Routine routine = (Routine) e.nextElement();

            if (!routine.getMethodName().equals("calcPrimeFactors")) continue;

            System.err.println("Method name: " + routine.getMethodName());
	        routine.addBefore("MyICount", "mcount", new Integer(1));
            //routine.addBefore("MyICount", "printArg", new Integer(0))

            routine.addAfter("MyICount", "printICount", routine.getMethodName());
            
            for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements(); ) {
                BasicBlock bb = (BasicBlock) b.nextElement();
                bb.addBefore("MyICount", "count", new Integer(bb.size()));
            }
        }

        //ci.addAfter("MyICount", "printICount", ci.getClassName());

        String outputDir = (argv.length > 0) ? argv[0] : ".";

        ci.write(outputDir + System.getProperty("file.separator") + "IntFactorization.class");
       
    }
    
    public static synchronized void printICount(String foo) {
        //System.out.println(i_count + " instructions in " + b_count + " basic blocks were executed in " + m_count + " methods.");

        if (i_count < 2) {
            i_count = 0;
            return;
        }

        String output = i_count + " instructions\n";
        Writer outputWriter;
        try {
            outputWriter = new BufferedWriter(new FileWriter("log.txt", true));
            outputWriter.append(output);
            outputWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        i_count = 0;

    }
    

    public static synchronized void count(int incr) {
        i_count += incr;
        b_count++;
    }

    public static synchronized void mcount(int incr) {
		m_count++;
    }

}

