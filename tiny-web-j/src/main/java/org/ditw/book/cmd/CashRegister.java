package org.ditw.book.cmd;

public class CashRegister {
    public int getSum() {
        return _sum;
    }

    private int _sum;

    public CashRegister() {
        _sum = 0;
    }

    public void add(int amount) {
        _sum += amount;
    }

    public void deduct(int amount) {
        if (_sum < amount) throw new IllegalArgumentException("Not enough money left");

        _sum -= amount;
    }

    public void trace(String msg) {
        System.out.println(String.format("%s: %d\n", msg, _sum));
    }
}
