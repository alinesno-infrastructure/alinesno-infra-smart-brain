package com.alinesno.infra.smart.brain.console.menus;

import lombok.Data;

import java.util.List;

@Data
public class Menu {
        private String name;
        private String path;
        private boolean hidden;
        private String redirect;
        private String component;
        private boolean alwaysShow;
        private Meta meta;
        private List<Menu> children;

    public Menu(String name, String component, boolean hidden, String path, Meta meta) {
        this.name = name;
        this.path = path;
        this.hidden = hidden;
        this.component = component;
        this.alwaysShow = true ;
        this.meta = meta;
    }

    @Data
        public static class Meta {
            private String title;
            private String icon;
            private boolean noCache;
            private String link;

            // Getters and setters

            public Meta(String title, String icon, boolean noCache, String link) {
                this.title = title;
                this.icon = icon;
                this.noCache = noCache;
                this.link = link;
            }
        }

        public Menu(String name, String path, boolean hidden, String redirect, String component, boolean alwaysShow, Meta meta, List<Menu> children) {
            this.name = name;
            this.path = path;
            this.hidden = hidden;
            this.redirect = redirect;
            this.component = component;
            this.alwaysShow = alwaysShow;
            this.meta = meta;
            this.children = children;
        }
    }