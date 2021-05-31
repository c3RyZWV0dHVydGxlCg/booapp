package com.pavelmakhov.booapp;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"com.pavelmakhov.booapp.steps"},
        features = {"classpath:features"},
        plugin = {"pretty", "html:target/cucumber"}
)
public class CucumberTest {
}
