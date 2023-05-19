package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.io.Serializable;

public class IdVersionKey implements Serializable {
    private Long id;
    private Integer version;

    public IdVersionKey(Long id, Integer version) {
        this.id = id;
        this.version = version;
    }

    public IdVersionKey() {
        this.id = 0L;
        this.version = 0;
    }
}
