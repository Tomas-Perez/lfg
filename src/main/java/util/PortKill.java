package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Tomas Perez Molina
 */
public class PortKill {

    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter port number:");
        final int port = scanner.nextInt();

        final Process p = Runtime.getRuntime().exec("netstat -a -o -n");
        GetPID getPID = new GetPID(p, port);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> futurePID = executor.submit(getPID);

        p.waitFor();
        executor.shutdown();

        final Integer pid = futurePID.get();

        Process kill = null;

        if(pid == null){
            System.out.println("Something went terribly wrong");
        } else if(pid == -1){
            System.out.printf("No process in port %d\n", port);
        } else {
            System.out.printf("Kill process %d in port %d? (Y/N) \n", pid, port);
            switch (scanner.next().toLowerCase()){
                case "y":
                    System.out.printf("Killing process %d...\n", pid);
                    kill = Runtime.getRuntime().exec(String.format("taskkill /F /PID %d", pid));
                    break;
                case "n":
                    System.out.println("Pussy");
                    break;
                default:
                    System.out.println("Enter Y/N");
                    break;
            }
        }

        if(kill != null){
            kill.waitFor();
        }
    }

    private static class GetPID implements Callable<Integer>{

        private Process p;
        private int port;

        private GetPID(Process p, int port) {
            this.p = p;
            this.port = port;
        }

        @Override
        public Integer call(){
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String processAddress = String.format("0.0.0.0:%d", port);

            try {
                while ((line = input.readLine()) != null) {
                    String trimmed = line.trim().replaceAll(" +", " ");
                    final String[] split = trimmed.split(" ");
                    if(split.length == 5 && split[1].equals(processAddress)){
                        System.out.println(Arrays.toString(split));
                        return Integer.parseInt(split[4]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }
    }
}
