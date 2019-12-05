package com.tr.hsyn.telefonrehberi.util.immutable;

import org.immutables.value.Value;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.CLASS)
@Value.Style(
      typeAbstract = {"I*", "Abstract*"}, // 'Abstract' prefix will be detected and trimmed
      typeImmutable = "*", // No prefix or suffix for generated immutable type
      defaults = @Value.Immutable(copy = false, builder = false)
)
public @interface StdObj{}
