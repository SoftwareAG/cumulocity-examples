package com.cumulocity.route.model.core;

import lombok.Data;

@Data
public class Section<T, L> {
    private final T begin;
    private final L end;
}
