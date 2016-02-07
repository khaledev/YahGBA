package ygba.time;

import ygba.memory.Memory;

public final class Time {
    
    private Timer[] timer;
    
    
    public Time() {
        timer = new Timer[4];
        timer[3] = new Timer(null,     3);
        timer[2] = new Timer(timer[3], 2);
        timer[1] = new Timer(timer[2], 1);
        timer[0] = new Timer(timer[1], 0);
    }
    
    public void connectToMemory(Memory memory) {
        for (int i = 0; i < timer.length; i++) {
            timer[i].connectToMemory(memory);
        }
    }
    
    public Timer getTimer(int timerNumber) {
        return timer[timerNumber];
    }
    
    public void reset() {
        for (int i = 0; i < timer.length; i++) {
            timer[i].reset();
        }
    }
    
    public void addTime(int t) {
        for (int i = 0; i < timer.length; i++) {
            timer[i].addTime(t);
        }
    }
    
}