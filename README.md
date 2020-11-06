# information-retrieval
Information Retrieval: Project 1 - Lucene Basics

# Basic commands
```
> java -jar Interface.jar indexer <input_directory> <output_directory>
> java -jar Interface.jar querier <index_directory> <search terms>
```

# Example usage
Preparing the xml files and index
```
> BigXMLReader.exe 0.99 ./dump/Posts.xml ./dump/xml/
> java -jar Interface.jar indexer ./dump/xml/ ./index/
```

Search for the term 'lucene':
```
> java -jar Interface.jar querier ./index/ lucene
```
Searching on multiple terms 'stack' and 'overflow':
```
> java -jar Interface.jar querier ./index/ stack overflow
```
Search on multiple words in one term 'stack overflow'
```
> java -jar Interface.jar querier ./index/ "stack overflow"
```
Search on exact match of term 'lucene'
```
> java -jar Interface.jar querier ./index/ '"lucene"'
```
Search on term 'lucene' but can't include 'python'
```
> java -jar Interface.jar querier ./index/ lucene -python
```
