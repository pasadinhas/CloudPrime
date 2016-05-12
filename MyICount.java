import BIT.highBIT.*;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.concurrent.ThreadFactory;
import cnv.DBWriter;

public class MyICount {
    private static PrintStream out = null;
    private static Map counter = new HashMap();
    private static Map input = new HashMap();
    private static int b_count = 0, m_count = 0;

    public static void main(String argv[]) {

        ClassInfo ci = new ClassInfo("IntFactorization.class");

        for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements(); ) {
            Routine routine = (Routine) e.nextElement();

            if (!routine.getMethodName().equals("calcPrimeFactors")) continue;

            System.err.println("Method name: " + routine.getMethodName());

            routine.addAfter("MyICount", "printICount", routine.getMethodName());

            for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements(); ) {
                BasicBlock bb = (BasicBlock) b.nextElement();
                bb.addBefore("MyICount", "count", new Integer(1));
            }
        }

        String outputDir = (argv.length > 0) ? argv[0] : ".";

        ci.write(outputDir + System.getProperty("file.separator") + "IntFactorization.class");
    }

    public static synchronized void printICount(String foo) {
        //System.out.println(i_count + " instructions in " + b_count + " basic blocks were executed in " + m_count + " methods.");
        Long threadID = new Long(Thread.currentThread().getId());

        BigInteger i_count = (BigInteger) counter.get(threadID);
        if (i_count.equals(new BigInteger("0")) || i_count.equals(new BigInteger("1"))) {
            counter.put(threadID, new BigInteger("0"));
            return;
        }
        try {
            DBWriter.write(""+input.get(threadID), ""+i_count);
        }catch (IOException e) {
            System.out.println("Failed to write to databaste");
            e.printStackTrace();
        }

        counter.put(threadID, new BigInteger("0"));
    }


    public static synchronized void count(int incr) {
        Long threadID = new Long(Thread.currentThread().getId());
        BigInteger current = (BigInteger) counter.get(threadID);
        if (current == null) {
            current = new BigInteger("0");
        }
        BigInteger newValue = current.add(BigInteger.valueOf(incr));
        counter.put(threadID, newValue);
    }

    public static synchronized void mcount(int incr) {
		m_count++;
    }

    public static synchronized void registerInput(String num) {
        Long threadID = new Long(Thread.currentThread().getId());
        input.put(threadID, num);
    }
}
