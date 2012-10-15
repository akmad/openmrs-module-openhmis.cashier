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
package org.openmrs.module.openhmis.cashier.api.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.cashier.api.ITimesheetService;
import org.openmrs.module.openhmis.cashier.api.model.Timesheet;
import org.openmrs.module.openhmis.cashier.api.security.IDataAuthorizationPrivileges;
import org.openmrs.module.openhmis.cashier.api.util.CashierPrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TimesheetServiceImpl
		extends BaseDataServiceImpl<Timesheet>
		implements ITimesheetService, IDataAuthorizationPrivileges {

	@Override
	protected IDataAuthorizationPrivileges getPrivileges() {
		return this;
	}

	@Override
	protected void validate(Timesheet entity) throws APIException {
	}

	@Override
	public String getVoidPrivilege() {
		return CashierPrivilegeConstants.MANAGE_TIMESHEETS;
	}

	@Override
	public String getSavePrivilege() {
		return CashierPrivilegeConstants.MANAGE_TIMESHEETS;
	}

	@Override
	public String getPurgePrivilege() {
		return CashierPrivilegeConstants.PURGE_TIMESHEETS;
	}

	@Override
	public String getGetPrivilege() {
		return CashierPrivilegeConstants.VIEW_TIMESHEETS;
	}

	@Override
	public Timesheet getCurrentTimesheet(Provider cashier) {
		Criteria criteria = dao.createCriteria(Timesheet.class);
		criteria.add(Restrictions.and(
				Restrictions.eq("cashier", cashier),
				Restrictions.isNull("clockOut"))
		);
		criteria.addOrder(Order.desc("clockIn"));

		return dao.selectSingle(Timesheet.class, criteria);
	}
}
