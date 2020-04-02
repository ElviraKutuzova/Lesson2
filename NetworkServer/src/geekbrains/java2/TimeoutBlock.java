package geekbrains.java2;

public class TimeoutBlock {
    private final long timeoutMilliSeconds;
    private long timeoutInterval=100;

    public TimeoutBlock(long timeoutMilliSeconds){
        this.timeoutMilliSeconds=timeoutMilliSeconds;
    }

    public void addBlock(Runnable runnable) throws Throwable{
        long collectIntervals=0;
        Thread timeoutWorker=new Thread(runnable);
        timeoutWorker.start();
        do{
            if(collectIntervals>=this.timeoutMilliSeconds){
                timeoutWorker.stop();

                throw new Exception("<<<<<<<<<<****>>>>>>>>>>> Timeout Block Execution Time Exceeded In "+timeoutMilliSeconds+" Milli Seconds. Thread Block Terminated.");
            }
            collectIntervals+=timeoutInterval;
            Thread.sleep(timeoutInterval);

        }while(timeoutWorker.isAlive());
        System.out.println("<<<<<<<<<<####>>>>>>>>>>> Timeout Block Executed Within "+collectIntervals+" Milli Seconds.");

    }


    /**
     * @return the timeoutInterval
     */
    public long getTimeoutInterval() {
        return timeoutInterval;
    }

    /**
     * @param timeoutInterval the timeoutInterval to set
     */
    public void setTimeoutInterval(long timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }
}
//Обернуть блок в этот код
//try {
//        TimeoutBlock timeoutBlock = new TimeoutBlock(10 * 60 * 1000);//set timeout in milliseconds
//        Runnable block=new Runnable() {
//
//@Override
//public void run() {
//                      //TO DO write block of code to execute
//        }
//        };
//
//        timeoutBlock.addBlock(block);// execute the runnable block
//
//        } catch (Throwable e) {
//        //catch the exception here . Which is block didn't execute within the time limit
//        }

