package org.ditw.book.cmd;

public class Refund implements ICmd {

    private final CashRegister _cashRegister;
    private final int _amount;

    public Refund(CashRegister cashRegister, int amount) {
        _cashRegister = cashRegister;
        _amount = amount;
    }

    @Override
    public void Execute() {
        _cashRegister.deduct(_amount);
    }

    @Override
    public void Undo() {
        _cashRegister.add(_amount);
    }
}
