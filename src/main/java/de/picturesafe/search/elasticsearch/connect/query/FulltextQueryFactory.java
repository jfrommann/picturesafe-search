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

package de.picturesafe.search.elasticsearch.connect.query;

import de.picturesafe.search.elasticsearch.config.FieldConfiguration;
import de.picturesafe.search.elasticsearch.config.MappingConfiguration;
import de.picturesafe.search.elasticsearch.config.QueryConfiguration;
import de.picturesafe.search.elasticsearch.connect.dto.QueryDto;
import de.picturesafe.search.elasticsearch.connect.error.ElasticsearchException;
import de.picturesafe.search.elasticsearch.connect.util.FieldConfigurationUtils;
import de.picturesafe.search.elasticsearch.connect.util.PhraseMatchHelper;
import de.picturesafe.search.expression.Expression;
import de.picturesafe.search.expression.FulltextExpression;
import de.picturesafe.search.expression.ValueExpression;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.picturesafe.search.expression.ConditionExpression.Comparison.NOT_EQ;

@Component
public class FulltextQueryFactory implements QueryFactory {

    private QueryConfiguration queryConfig;
    private QuerystringPreprocessor preprocessor;

    @Autowired
    public FulltextQueryFactory(QueryConfiguration queryConfig) {
        this.queryConfig = queryConfig;
    }

    @Autowired
    public void setPreprocessor(QuerystringPreprocessor preprocessor) {
        this.preprocessor = preprocessor;
    }

    @Override
    public boolean supports(QueryDto queryDto) {
        final Expression expression = queryDto.getExpression();
        return expression instanceof FulltextExpression
                || (expression instanceof ValueExpression && ((ValueExpression) expression).getName().equals(FieldConfiguration.FIELD_NAME_FULLTEXT));
    }

    @Override
    public QueryBuilder create(QueryFactoryCaller caller, QueryDto queryDto, MappingConfiguration mappingConfiguration) {
        final FieldConfiguration fieldConfig = FieldConfigurationUtils.fieldConfiguration(mappingConfiguration, FieldConfiguration.FIELD_NAME_FULLTEXT, false);
        if (fieldConfig == null) {
            throw new ElasticsearchException("Missing field configuration for fulltext field, fulltext expressions are not supported without configuration!");
        }

        final ValueExpression valueExpression = (ValueExpression) queryDto.getExpression();
        final String value = valueExpression.getValue().toString();
        if (!StringUtils.isBlank(value)) {
            QueryStringQueryBuilder result = QueryBuilders.queryStringQuery(preprocess(value))
                    .field(FieldConfiguration.FIELD_NAME_FULLTEXT)
                    .defaultOperator(queryConfig.getDefaultQueryStringOperator());
            if (value.contains("*") || value.contains("?")) {
                result = result.analyzeWildcard(true);
            }

            if (valueExpression.getComparison() != null && valueExpression.getComparison().equals(NOT_EQ)) {
                return QueryBuilders.boolQuery().mustNot(result);
            } else {
                return result;
            }
        }
        return null;
    }

    private String preprocess(String queryString) {
        queryString = PhraseMatchHelper.replacePhraseMatchChars(queryString);
        if (preprocessor != null) {
            queryString = preprocessor.process(queryString);
        }
        return queryString;
    }
}
