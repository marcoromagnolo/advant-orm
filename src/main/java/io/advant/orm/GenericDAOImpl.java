/**
 * Copyright 2016 Advant I/O
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.advant.orm;

import io.advant.orm.exception.OrmException;
import io.advant.orm.internal.Conditions;

import java.util.List;

/**
 *
 * @param <T>
 */
public class GenericDAOImpl<T extends Entity> extends AbstractDAO<T> implements GenericDAO<T> {

    public GenericDAOImpl(Class<T> entityClass, DBConnection connection) {
        super(entityClass, connection);
    }

    @Override
    public List<T> find(T entityClass, Conditions conditions) throws OrmException {
        return null;
    }

    @Override
    public void update(T entityClass, Conditions conditions) throws OrmException {

    }

    @Override
    public void delete(T entityClass, Conditions conditions) throws OrmException {

    }
}
