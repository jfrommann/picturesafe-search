/*
 * Copyright 2020 picturesafe media/data/bank GmbH
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

package de.picturesafe.search.elasticsearch.connect.dto;

import de.picturesafe.search.elasticsearch.api.FacetItem;
import de.picturesafe.search.util.logging.CustomJsonToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FacetEntryDto implements FacetItem {

    private final Object value;
    private final long count;

    public FacetEntryDto(Object value, long count) {
        this.value = value;
        this.count = count;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FacetEntryDto)) {
            return false;
        } else {
            final FacetEntryDto target = (FacetEntryDto) o;
            return new EqualsBuilder().append(count, target.getCount()).append(value, target.getValue()).isEquals();
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(2621, 4271).append(value).append(count).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, new CustomJsonToStringStyle()) //--
                .append("value", value) //--
                .append("count", count) //--
                .toString();
    }
}
