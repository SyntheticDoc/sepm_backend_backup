package at.ac.tuwien.sepm.groupphase.backend.service.impl.logic;

import java.util.ArrayList;
import java.util.List;

public class ParserLiteralList {
    private List<Boolean> list;

    public void add(Boolean bool) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(bool);
    }

    public Integer countTrue() {
        if (list == null) {
            return 0;
        }
        return (int) list.stream().filter(b -> b).count();
    }
}
