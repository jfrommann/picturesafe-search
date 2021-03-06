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
package de.picturesafe.search.expression;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;

import static de.picturesafe.search.expression.ConditionExpression.Comparison.EQ;
import static de.picturesafe.search.expression.ConditionExpression.Comparison.GE;
import static de.picturesafe.search.expression.ConditionExpression.Comparison.GT;
import static de.picturesafe.search.expression.ConditionExpression.Comparison.LE;
import static de.picturesafe.search.expression.ConditionExpression.Comparison.LT;
import static de.picturesafe.search.expression.ConditionExpression.Comparison.NOT_EQ;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DayExpressionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeywordExpressionTest.class);
    private static final EnumSet<ConditionExpression.Comparison> VALID_COMPARISONS = EnumSet.of(EQ, NOT_EQ, GT, GE, LT, LE);

    @Test
    public void testAllowedComparisons() {
        final Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        VALID_COMPARISONS.forEach(comparison -> {
            LOGGER.debug("Testing comparison: {}", comparison);
            final DayExpression expression = new DayExpression("test", comparison, today);
            assertEquals(comparison, expression.getComparison());
        });
    }

    @Test
    public void testNotAllowedComparisons() {
        final Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        EnumSet.complementOf(VALID_COMPARISONS).forEach(comparison -> {
            LOGGER.debug("Testing comparison: {}", comparison);
            try {
                new DayExpression("test", comparison, today);
                fail("IllegalArgumentException should be thrown");
            } catch (IllegalArgumentException e) {
                LOGGER.debug("Caught expected exception", e);
            }
        });
    }
}
