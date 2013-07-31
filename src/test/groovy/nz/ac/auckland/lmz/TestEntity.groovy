package nz.ac.auckland.lmz

import nz.ac.auckland.lmz.persist.data.BasicEntity

class TestEntity implements BasicEntity<Long> {
    Long id;
    Date dateCreated = new Date();
    Date lastUpdated = new Date();
    Boolean enabled = true;
    Long version = 0;
}
