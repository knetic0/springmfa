package com.mehmetsolak.springmfa.application.core;

public interface CommandHandler<C extends Command<R>, R> {
    R execute(C command);
}
