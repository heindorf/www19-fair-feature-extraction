#!/bin/bash

# We require an installation of 7z for decompression.
if ! [ -x "$(command -v 7z)" ]; then
  echo "Error: 7z is not installed." >&2
  exit 1
fi

# External data
wdvc=../data/external/wdvc-2016/
wdvcDecompressed=../data/external/tmp/
wikidataDump=../data/external/wikidata-20160229-all.json.bz2
wdvdFeatures=../data/features/wdvd_features.csv.bz2

# Create required folders
mkdir -p ../data/item-properties
mkdir -p ../data/wikidata-graph
mkdir -p ../data/features/training/embeddings 
mkdir -p ../data/features/validation/embeddings 
mkdir -p ../data/features/test/embeddings 

mkdir -p ../data/external/tmp/training/
mkdir -p ../data/external/tmp/validation/
mkdir -p ../data/external/tmp/test/

# We decompress the wdvc corpus outside of java for runtime reasons.
# Therefore we direct the decompressed data to FIFO files ("similar to pipes", https://linux.die.net/man/3/mkfifo).
# The feature extraction (java) subsequently reads from these special files.
for dataset in "training/" "validation/" "test/"; do
	wdvcDatsetPath="$wdvc$dataset*.xml.7z"
	for filePath in $wdvcDatsetPath; do
		fifoName=$(basename -- "$filePath")
		fifoPath="../data/external/tmp/$dataset${fifoName%.*}"
		mkfifo $fifoPath # https://linux.die.net/man/3/mkfifo
		7z x $filePath -so > $fifoPath &
	done
done

# Compile
mvn package 

# Path to compiled jar
targetJar=./target/www19-fair-feature-extraction-1.0-SNAPSHOT-jar-with-dependencies.jar

# Start feature extraction
java -jar -Xmx230G -Xms230G $targetJar $wdvcDecompressed $wikidataDump $wdvdFeatures  2>&1 | tee log

function cleanUp {
	kill $(jobs -p)

	for dataset in "training/" "validation/" "test/"; do
		wdvcDatsetPath="$wdvc$dataset*.xml.7z"
		for filePath in $wdvcDatsetPath; do
			fifoName=$(basename -- "$filePath")
			fifoPath="../data/external/tmp/$dataset${fifoName%.*}"
			rm $fifoPath
		done
	done

	rmdir ../data/external/tmp/training/
	rmdir ../data/external/tmp/validation/
	rmdir ../data/external/tmp/test/
	rmdir ../data/external/tmp/
}

trap cleanUp EXIT
