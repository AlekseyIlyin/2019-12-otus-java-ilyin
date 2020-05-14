package ru.otus.model;

import lombok.*;
import org.bson.types.ObjectId;

@NoArgsConstructor
@Data
public class AddressDataSet{
    private ObjectId id;
    private String street;

    public AddressDataSet(String street) {
        this.street = street;
    }
}
