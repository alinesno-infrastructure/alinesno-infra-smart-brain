package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IndustryRoleCatalogDto {

    private Long id ;

    private String icon;

    private String name ;

    private int orderNum ;

    private String description;

    private List<IndustryRoleDto> agents = new ArrayList<>();

}