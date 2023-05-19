package at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses;

import java.util.ArrayList;

public class ParserModule {
    public String internalName;
    public ArrayList<ParserQuestion> questions;

    public ParserModule() {
        this.internalName = "A";
    }

    public ParserModule(String internalName) {
        this.internalName = internalName;
    }

    @Override
    public String toString() {
        return "ParserModule{"
            + "internalName='" + internalName + '\''
            + ", questions=" + questions
            + '}';
    }
}
