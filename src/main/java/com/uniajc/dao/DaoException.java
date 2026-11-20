package com.uniajc.dao;

public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public DaoException(String mensaje) {
        super(mensaje);
    }
}
