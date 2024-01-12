/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawaiiframework.sql;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResourceLoader;

/**
 * Unit tests for {@link ResourceSqlQueryResolver}.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
public class ResourceSqlQueryResolverTests {

  private ResourceSqlQueryResolver queryResolver;
  private String sqlFileName;
  private Path sqlFile;
  private String prefix;
  private String suffix;
  private String query1 = "QUERY1";
  private String query2 = "QUERY2";

  @Before
  public void setup() throws Exception {
    queryResolver = new ResourceSqlQueryResolver(new FileSystemResourceLoader());

    suffix = ".sql";
    sqlFile = Files.createTempFile("junit-test", suffix);

    // We're using a path outside the VM working dir, so prefix starts with file://
    prefix = "file:" + sqlFile.getParent() + '/';

    sqlFileName = sqlFile.getFileName().toString();
    sqlFileName = sqlFileName.substring(0, sqlFileName.length() - suffix.length());

    queryResolver.setPrefix(prefix);
    queryResolver.setSuffix(suffix);
    // Enable TRACE level for test debugging
    // Logger logger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    // logger.setLevel(Level.TRACE);
  }

  @After
  public void tearDown() throws IOException {
    Files.delete(sqlFile);
  }

  private void writeStringToFile(String contents) throws IOException {
    Files.writeString(sqlFile, contents);
  }

  @Test
  public void testFileIsReadOk() throws Exception {
    writeStringToFile(query1);
    String query = queryResolver.resolveSqlQuery(sqlFileName);
    assertThat(query, is(equalTo(query1)));
  }

  @Test
  public void testNonExistentFile() {
    String query = queryResolver.resolveSqlQuery(sqlFileName + "idontexist");
    assertThat(query, is(emptyOrNullString()));
  }

  @Test
  public void testOldContentsAreReturnedWithinCacheTime() throws Exception {
    queryResolver.setCacheSeconds(20);

    writeStringToFile(query1);
    String query = queryResolver.resolveSqlQuery(sqlFileName);
    assertThat(query, is(equalTo(query1)));
    writeStringToFile(query2);
    query = queryResolver.resolveSqlQuery(sqlFileName);
    assertThat(query, is(equalTo(query1)));
  }

  @Test
  public void testNewContentsAreReturnedAfterCacheTime() throws Exception {
    queryResolver.setCacheSeconds(1);
    writeStringToFile(query1);
    String query = queryResolver.resolveSqlQuery(sqlFileName);
    assertThat(query, is(equalTo(query1)));
    // make sure the file timestamp will be at least a second higher
    TimeUnit.SECONDS.sleep(1);
    writeStringToFile(query2);
    query = queryResolver.resolveSqlQuery(sqlFileName);
    assertThat(query, is(equalTo(query2)));
  }

  @Test
  public void testOldContentsAreReturnedDuringConcurrentUpdate() throws Exception {
    // Custom resolver for this test
    queryResolver = new LongUpdatingQueryResolver(100);
    queryResolver.setPrefix(prefix);
    queryResolver.setSuffix(suffix);
    // Disable cache seconds, i.e. the loader will always check if the query file has changed
    queryResolver.setCacheSeconds(0);

    // Setup initial query
    writeStringToFile(query1);
    assertThat(queryResolver.resolveSqlQuery(sqlFileName), is(equalTo("QUERY1")));

    // make sure the file timestamp will be at least a second higher
    TimeUnit.SECONDS.sleep(1);
    writeStringToFile(query2);

    ExecutorService executor = Executors.newFixedThreadPool(2);

    Callable<String> queryReader = () -> queryResolver.resolveSqlQuery(sqlFileName);

    Future<String> future1 = executor.submit(queryReader);
    Future<String> future2 = executor.submit(queryReader);

    /*
     * We don't know which thread will have returned the
     * updated query and which got the original one,
     * so just sort the result array and match it to the
     * array of expected values.
     */
    String[] returnedQueries = {future1.get(), future2.get()};
    Arrays.sort(returnedQueries);
    String[] expectedQueries = {query1, query2};
    assertArrayEquals(expectedQueries, returnedQueries);
  }

  /*
   * Subclass ResourceSqlQueryResolver to make doRefreshQueryHolder
   * wait a while, so we can test if contention between threads is
   * handled correctly.
   */
  private static class LongUpdatingQueryResolver extends ResourceSqlQueryResolver {

    private final int waitMillis;

    private LongUpdatingQueryResolver(int waitMillis) {
      super(new FileSystemResourceLoader());
      this.waitMillis = waitMillis;
    }

    @Override
    protected void doRefreshQueryHolder(String sqlQueryName, QueryHolder queryHolder) {
      try {
        // First wait the specified amount
        TimeUnit.MILLISECONDS.sleep(this.waitMillis);
        // Now actually read the file
        super.doRefreshQueryHolder(sqlQueryName, queryHolder);
      } catch (InterruptedException e) {
        throw new IllegalStateException("task interrupted", e);
      }
    }
  }
}
