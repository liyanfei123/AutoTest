package com.testframe.autotest.core.meta.convertor;

public interface ConverterI<PO, DO, DTO> {

    DO PoToDo(PO data);

    PO DoToPo(DO data);

    DO DtoToDo(DTO data);

    DTO DoToDto(DO data);
}
