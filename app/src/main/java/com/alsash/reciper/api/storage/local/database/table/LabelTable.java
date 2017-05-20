package com.alsash.reciper.api.storage.local.database.table;

import com.alsash.reciper.api.storage.local.database.converter.GuidConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import java.util.UUID;

/**
 * Local relational database table that represents the Label entity
 */
@Entity(nameInDb = "LABEL")
public class LabelTable {
    @Id
    private Long id;
    @Unique
    @Convert(converter = GuidConverter.class, columnType = String.class)
    private UUID uuid;
    private String name;
    private Date creationDate;
    private Date changeDate;

    @Generated(hash = 1487967186)
    public LabelTable(Long id, UUID uuid, String name, Date creationDate,
                      Date changeDate) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.creationDate = creationDate;
        this.changeDate = changeDate;
    }
    @Generated(hash = 893165010)
    public LabelTable() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public UUID getUuid() {
        return this.uuid;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getCreationDate() {
        return this.creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public Date getChangeDate() {
        return this.changeDate;
    }
    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }
}
