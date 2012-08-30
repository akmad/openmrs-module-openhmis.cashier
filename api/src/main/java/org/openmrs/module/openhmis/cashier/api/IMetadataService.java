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

package org.openmrs.module.openhmis.cashier.api;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.cashier.api.db.IEntityDao;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IMetadataService<T extends IEntityDao, E extends BaseOpenmrsMetadata> extends IEntityService<T, E> {
	/**
	 * Retires the specified entity. This effectively removes the entity from circulation or use.
	 *
	 * @param entity entity to be retired.
	 * @param reason the reason why the entity is being retired.
	 * @return the newly retired entity.
	 * @should retire the entity successfully
	 * @should throw IllegalArgumentException when the entity is null
	 * @should throw IllegalArgumentException when no reason is given
	 */
	//@Authorized( { CashierPrivilegeConstants.MANAGE_ITEMS })
	E retire(E entity, String reason) throws APIException;

	/**
	 * Unretire the specified entity. This restores a previously retired entity back into circulation and use.
	 *
	 * @param entity The entity to unretire.
	 * @return the newly unretired entity.
	 * @throws APIException
	 * @should throw IllegalArgumentException if the entity is null
	 * @should unretire the entity
	 */
	//@Authorized( { CashierPrivilegeConstants.MANAGE_ITEMS })
	E unretire(E entity) throws APIException;

	/**
	 * Returns all entity records that have the specified retirement status.
	 * @param retired {@code true} to return only retired entities, {@code false} to return only unretired entities.
	 * @return All the entity records that have the specified retirement status.
	 * @throws APIException
	 */
	List<E> getAll(boolean retired) throws APIException;

	/**
	 * Finds all the entities that start with the specified name.
	 * @param nameFragment The name fragment.
	 * @param includeRetired Whether retired item should be included in the results.
	 * @return All items that start with the specified name.
	 * @throws APIException
	 * @should throw IllegalArgumentException if the name is null
	 * @should throw IllegalArgumentException if the name is empty
	 * @should throw IllegalArgumentException if the name is longer than 255 characters
	 * @should return an empty list if no entities are found
	 * @should not return retired entities unless specified
	 * @should return entities that start with the specified name
	 */
	List<E> findByName(String nameFragment, boolean includeRetired) throws APIException;
}