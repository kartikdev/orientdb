package com.orientechnologies.lucene.analyzer;

import com.orientechnologies.common.io.OIOUtils;
import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static com.orientechnologies.lucene.analyzer.OLuceneAnalyzerFactory.AnalyzerKind.INDEX;
import static com.orientechnologies.lucene.analyzer.OLuceneAnalyzerFactory.AnalyzerKind.QUERY;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by frank on 30/10/2015.
 */
public class OLuceneAnalyzerFactoryTest {

  private OLuceneAnalyzerFactory analyzerFactory;
  private ODocument              metadata;
  private OIndexDefinition       indexDef;

  @Before
  public void before() throws IOException {

    analyzerFactory = new OLuceneAnalyzerFactory();

    //default analyzer is Standard
    //default analyzer for indexing is keyword
    //default analyzer for query is standard

    String metajson = OIOUtils.readFileAsString(new File("./src/test/resources/index_metadata_new.json"));

    metadata = new ODocument().fromJSON(metajson);

    indexDef = Mockito.mock(OIndexDefinition.class);

    when(indexDef.getFields()).thenReturn(asList("title", "author", "lyrics", "genre"));
    when(indexDef.getClassName()).thenReturn("Song");

  }

  @Test
  public void jsonTest() throws Exception {

    ODocument doc = new ODocument().fromJSON("{\n" + "  \"index_analyzer\": \"org.apache.lucene.analysis.en.EnglishAnalyzer\",\n"
        + "  \"query_analyzer\": \"org.apache.lucene.analysis.standard.StandardAnalyzer\",\n"
        + "  \"name_index_analyzer\": \"org.apache.lucene.analysis.standard.StandardAnalyzer\",\n"
        + "  \"name_query_analyzer\": \"org.apache.lucene.analysis.core.KeywordAnalyzer\",\n"
        + "  \"description_index_analyzer\": {\n" + "    \"class\": \"org.apache.lucene.analysis.standard.StandardAnalyzer\",\n"
        + "    \"stopwords\": [\n" + "      \"the\",\n" + "      \"is\"\n" + "    ]\n" + "  }\n" + "}", "noMap");

    System.out.println(doc.toJSON());

    ODocument description_index_analyzer = doc.field("description_index_analyzer");
    //    ODocument index_analyzer = doc.field("index_analyzer");

    System.out.println(description_index_analyzer.toJSON());
  }

  @Test
  public void shouldAssignStandardAnalyzerForIndexingUndefined() throws Exception {

    OLucenePerFieldAnalyzerWrapper analyzer = (OLucenePerFieldAnalyzerWrapper) analyzerFactory
        .createAnalyzer(indexDef, INDEX, metadata);
    //default analyzer for indexing
    assertThat(analyzer.getWrappedAnalyzer("undefined")).isInstanceOf(StandardAnalyzer.class);

  }

  @Test
  public void shouldAssignKeywordAnalyzerForIndexing() throws Exception {

    OLucenePerFieldAnalyzerWrapper analyzer = (OLucenePerFieldAnalyzerWrapper) analyzerFactory
        .createAnalyzer(indexDef, INDEX, metadata);
    //default analyzer for indexing
    assertThat(analyzer.getWrappedAnalyzer("genre")).isInstanceOf(KeywordAnalyzer.class);

  }

  @Test
  public void shouldAssignConfiguredAnalyzerForIndexing() throws Exception {

    OLucenePerFieldAnalyzerWrapper analyzer = (OLucenePerFieldAnalyzerWrapper) analyzerFactory
        .createAnalyzer(indexDef, INDEX, metadata);
    assertThat(analyzer.getWrappedAnalyzer("title")).isInstanceOf(EnglishAnalyzer.class);

    assertThat(analyzer.getWrappedAnalyzer("author")).isInstanceOf(KeywordAnalyzer.class);

    assertThat(analyzer.getWrappedAnalyzer("lyrics")).isInstanceOf(EnglishAnalyzer.class);

  }

  @Test
  public void shouldAssignConfiguredAnalyzerForQuery() throws Exception {

    OLucenePerFieldAnalyzerWrapper analyzer = (OLucenePerFieldAnalyzerWrapper) analyzerFactory
        .createAnalyzer(indexDef, QUERY, metadata);
    assertThat(analyzer.getWrappedAnalyzer("title")).isInstanceOf(EnglishAnalyzer.class);

    assertThat(analyzer.getWrappedAnalyzer("author")).isInstanceOf(KeywordAnalyzer.class);

    assertThat(analyzer.getWrappedAnalyzer("genre")).isInstanceOf(StandardAnalyzer.class);

  }

  @Test
  public void shouldUseClassNameToPrefixFieldName() throws Exception {

    //modify metadata to force use of class name as prefix
    metadata.field("prefix_with_class_name", Boolean.TRUE);

    OLucenePerFieldAnalyzerWrapper analyzer = (OLucenePerFieldAnalyzerWrapper) analyzerFactory
        .createAnalyzer(indexDef, QUERY, metadata);
    assertThat(analyzer.getWrappedAnalyzer("Song.title")).isInstanceOf(EnglishAnalyzer.class);

    assertThat(analyzer.getWrappedAnalyzer("Song.author")).isInstanceOf(KeywordAnalyzer.class);

    assertThat(analyzer.getWrappedAnalyzer("Song.genre")).isInstanceOf(StandardAnalyzer.class);

  }

}