package com.example.vinfan.mapper;

import java.util.List;

public interface BaseMapper <D, E>{
     D convertDTO(E entity);
     List<D> convertDTO(List<E> listEntity);
}
