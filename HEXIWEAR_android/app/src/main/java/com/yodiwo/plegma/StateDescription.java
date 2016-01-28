package com.yodiwo.plegma;

/**
 * Created by ApiGenerator Tool (Java) on 28/08/2015 18:34:50.
 */

public class StateDescription {

    public double Minimum;

    public double Maximum;

    public double Step;

    public String Pattern;

    public Boolean ReadOnly;

    public StateDescription() {
    }

    public StateDescription(double Minimum, double Maximum, double Step, String Pattern, Boolean ReadOnly) {
        this.Minimum = Minimum;
        this.Maximum = Maximum;
        this.Step = Step;
        this.Pattern = Pattern;
        this.ReadOnly = ReadOnly;

    }

}
