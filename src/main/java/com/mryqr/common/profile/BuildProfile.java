package com.mryqr.common.profile;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.context.annotation.Profile;

@Retention(RetentionPolicy.RUNTIME)
@Profile("build")
public @interface BuildProfile {
}
