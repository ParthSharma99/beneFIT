package tech.iosd.benefit.Model;

public class Stopwatch
{
        private long startTime = 0;
        private boolean running = false;
        private long currentTime = 0;

        public void start() {
            this.startTime = System.currentTimeMillis();
            this.running = true;
        }

        public void stop() {
            this.running = false;
        }

        public void pause() {
            this.running = false;
            currentTime = System.currentTimeMillis() - startTime;
        }
        public void resume() {
            this.running = true;
            this.startTime = System.currentTimeMillis() - currentTime;
        }

        public long getElapsedTimeMili() {
            long elapsed = 0;
            if (running) {
                elapsed =((System.currentTimeMillis() - startTime)/100) % 1000 ;
            }
            else
                elapsed =(currentTime/100) % 1000;
            return elapsed;
        }

        public long getElapsedTimeSecs() {
            long elapsed = 0;
            if (running) {
                elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
            }
            else
                elapsed=((currentTime / 1000) % 60);
            return elapsed;
        }

    public double getTotalElapsedTimeSecs() {
        long elapsed = 0;double totalElapsed=0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000);
        }
        else
            elapsed=((currentTime / 1000));
        totalElapsed=(double)elapsed;
        return totalElapsed;
    }

        public long getElapsedTimeMin() {
            long elapsed = 0;
            if (running) {
                elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60 ) % 60;
            }
            else
                elapsed=(((currentTime/ 1000) / 60 ) % 60);
            return elapsed;
        }

        public long getElapsedTimeHour() {
            long elapsed = 0;
            if (running) {
                elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60 ) / 60);
            }
            else
                elapsed=(((currentTime/ 1000) / 60 ) / 60);
            return elapsed;
        }

        public String toString()
        {
            String result = "00:00";
                if (getElapsedTimeSecs() == 0 && getElapsedTimeMin() == 0 && getElapsedTimeSecs() == 0)
                    result = "00:00";
                else if (getElapsedTimeMin() == 0 && getElapsedTimeHour() == 0)
                {
                    if(getElapsedTimeSecs()<10)
                        result="00" + ":0" + getElapsedTimeSecs();
                    else
                    result = "00" + ":" + getElapsedTimeSecs();
                }
                else if (getElapsedTimeHour() == 0)
                {
                    if(getElapsedTimeSecs()<10 && getElapsedTimeMin()<10)
                        result="0"+getElapsedTimeMin() + ":0" + getElapsedTimeSecs();
                    else if(getElapsedTimeSecs()<10)
                        result=getElapsedTimeMin() + ":0" + getElapsedTimeSecs();
                    else if(getElapsedTimeMin()<10)
                        result="0"+getElapsedTimeMin() + ":" + getElapsedTimeSecs();
                    else
                    result = getElapsedTimeMin() + ":" + getElapsedTimeSecs();
                }
                else
                    {
                        if(getElapsedTimeHour()<10 && getElapsedTimeSecs()<10 && getElapsedTimeMin()<10)
                            result="0"+getElapsedTimeHour() + ":" +"0"+getElapsedTimeMin() + ":0" + getElapsedTimeSecs();
                        else if(getElapsedTimeHour()<10 && getElapsedTimeSecs()<10)
                            result="0"+getElapsedTimeHour() + ":" +getElapsedTimeMin() + ":0" + getElapsedTimeSecs();
                        else if(getElapsedTimeHour()<10 && getElapsedTimeMin()<10)
                            result="0"+getElapsedTimeHour() + ":" +"0"+getElapsedTimeMin() + ":" + getElapsedTimeSecs();
                        else if(getElapsedTimeSecs()<10 && getElapsedTimeMin()<10)
                            result=getElapsedTimeHour() + ":" +"0"+getElapsedTimeMin() + ":0" + getElapsedTimeSecs();
                        else if(getElapsedTimeSecs()<10)
                            result=getElapsedTimeHour() + ":" +getElapsedTimeMin() + ":0" + getElapsedTimeSecs();
                        else if(getElapsedTimeMin()<10)
                            result=getElapsedTimeHour() + ":" +"0"+getElapsedTimeMin() + ":" + getElapsedTimeSecs();
                        else if(getElapsedTimeHour()<10)
                            result = "0"+getElapsedTimeHour() + ":" + getElapsedTimeMin() + ":" + getElapsedTimeSecs();
                        else
                            result = getElapsedTimeHour() + ":" + getElapsedTimeMin() + ":" + getElapsedTimeSecs();
                }
            return result;
        }
}
