package org.dailycodebuffer.codebufferspringbootmongodb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;

/**
 * PageableHandlerMethodArgumentResolver pageableResolver
 * PagedResourcesAssembler pagedResourcesAssembler
 * both are already configured Bean by Autoconfiguration
 */
//@Configuration
public class PagedResourceAssemblerConfig {

    /*private final PageableHandlerMethodArgumentResolver pageableResolver;
    private final PagedResourcesAssembler pagedResourcesAssembler;

    public PagedResourceAssemblerConfig(PageableHandlerMethodArgumentResolver pageableResolver, PagedResourcesAssembler pagedResourcesAssembler) {
        this.pageableResolver = pageableResolver;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }*/

    /*@Bean
    public PagedResourcesAssembler<?> pagedResourcesAssembler() {
        return new PagedResourcesAssembler<>((HateoasPageableHandlerMethodArgumentResolver) pageableResolver, null);
    }*/

    /*@Bean
    public PageableHandlerMethodArgumentResolver pageableResolver() {
        return new PageableHandlerMethodArgumentResolver();
    }*/
}
