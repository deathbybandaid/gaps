package com.jasonhhouse.gaps.controller;

import com.jasonhhouse.gaps.*;
import com.jasonhhouse.gaps.service.BindingErrorsService;
import com.jasonhhouse.gaps.validator.PlexLibrariesValidator;
import com.jasonhhouse.gaps.validator.PlexPropertiesValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/plexMovieList")
public class PlexMovieListController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlexMovieListController.class);

    private final BindingErrorsService bindingErrorsService;
    private final PlexQuery plexQuery;
    private final GapsService gapsService;

    @Autowired
    public PlexMovieListController(BindingErrorsService bindingErrorsService, PlexQuery plexQuery, GapsService gapsService) {
        this.bindingErrorsService = bindingErrorsService;
        this.plexQuery = plexQuery;
        this.gapsService = gapsService;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView postPlexMovieList(@Valid @ModelAttribute("plexSearch") PlexSearch plexSearch, BindingResult bindingResult) {
        LOGGER.info("postPlexMovieList( " + plexSearch + " )");

        if (bindingErrorsService.hasBindingErrors(bindingResult)) {
            return bindingErrorsService.getErrorPage();
        }

        List<PlexLibrary> plexLibraries = plexQuery.getLibraries(gapsService.getPlexSearch());
        gapsService.updateLibrarySelections(plexLibraries);
        gapsService.updatePlexSearch(plexSearch);

        ModelAndView modelAndView = new ModelAndView("plexMovieList");
        LOGGER.info(gapsService.getPlexSearch().toString());
        modelAndView.addObject("plexSearch", gapsService.getPlexSearch());
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPlexMovieList() {
        LOGGER.info("getPlexMovieList()");
        return new ModelAndView("plexMovieList");
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        LOGGER.info("initBinder()");
        binder.addCustomFormatter(new PlexSearchFormatter(), "plexSearch");
        binder.setValidator(new PlexLibrariesValidator());
    }
}
