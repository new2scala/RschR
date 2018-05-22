package org.ditw.book.cmd;

public class Purchase implements ICmd {
    private final CashRegister _cashRegister;
    private final int _amount;

    public Purchase(CashRegister cashRegister, int amount) {
        _cashRegister = cashRegister;
        _amount = amount;
    }

    @Override
    public void Execute() {
        _cashRegister.add(_amount);
    }

    @Override
    public void Undo() {
        _cashRegister.deduct(_amount);
    }
}
