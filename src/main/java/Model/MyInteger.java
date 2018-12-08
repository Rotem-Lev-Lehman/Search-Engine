package Model;

public class MyInteger {
    private volatile int value;

    public MyInteger(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void add(int value){
        this.value += value;
    }

    public int getValue(){
        return value;
    }
}
