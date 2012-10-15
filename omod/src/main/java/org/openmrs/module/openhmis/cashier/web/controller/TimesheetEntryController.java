/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.cashier.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.ProviderService;
import org.openmrs.module.openhmis.cashier.api.ICashPointService;
import org.openmrs.module.openhmis.cashier.api.ITimesheetService;
import org.openmrs.module.openhmis.cashier.api.model.Timesheet;
import org.openmrs.module.openhmis.cashier.api.util.ProviderHelper;
import org.openmrs.module.openhmis.cashier.web.CashierWebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@Controller
@RequestMapping(value = CashierWebConstants.TIMESHEET_ENTRY_PAGE)
public class TimesheetEntryController {
	private ITimesheetService timesheetService;
	private ICashPointService cashPointService;
	private ProviderService providerService;

	@Autowired
	public TimesheetEntryController(ITimesheetService timesheetService,
	                                ICashPointService cashPointService,
	                                ProviderService providerService) {
		this.timesheetService = timesheetService;
		this.cashPointService = cashPointService;
		this.providerService = providerService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void render(@RequestParam(value = "providerId", required = false) Integer providerId,
	                   @RequestParam(value = "returnUrl", required = false) String returnUrl,
	                   ModelMap modelMap) {
		Provider provider;
		if (providerId != null) {
			provider = providerService.getProvider(providerId);
		} else {
			provider = ProviderHelper.getCurrentProvider(providerService);
		}

		if (provider == null) {
			throw new APIException("Could not locate the provider.");
		}

		Timesheet timesheet = timesheetService.getCurrentTimesheet(provider);
		if (timesheet == null) {
			timesheet = new Timesheet();
			timesheet.setCashier(provider);
			timesheet.setClockIn(new Date());
		}

		modelMap.addAttribute("cashPoints", cashPointService.getAll());
		modelMap.addAttribute("returnUrl", returnUrl);
		modelMap.addAttribute("timesheet", timesheet);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(Timesheet timesheet, Errors errors, WebRequest request) {
		new TimesheetEntryValidator().validate(timesheet, errors);
		if (errors.hasErrors()) {
			return null;
		}

		timesheetService.save(timesheet);

		String returnUrl = request.getParameter("returnUrl");
		if (StringUtils.isEmpty(returnUrl)) {
			returnUrl = request.getContextPath() + "/";
		} else {
			returnUrl = CashierWebConstants.redirectUrl(returnUrl);
		}
		return returnUrl;
	}
}

