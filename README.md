Debiasing Vandalism Detection Models at Wikidata: Feature Extraction
===============================================================================

The Wikidata Vandalism Detectors FAIR-E and FAIR-S are machine learning models for automatic vandalism detection in Wikidata without discriminating against anonymous editors. They were developed as a joint project between Paderborn University and Leipzig University.

This is the feature extraction component that extracts features for FAIR-E and FAIR-S. Classification and evaluation for FAIR-E, FAIR-S and the baselines WDVD, ORES, and FILTER can be done with the corresponding [classification and evaluation component](https://github.com/heindorf/www19-fair-classification).

Paper
-----

This source code forms the basis for our WWW 2019 paper [Debiasing Vandalism Detection Models at Wikidata](https://doi.org/10.1145/3308558.3313507). When using the code, please make sure to refer to it as follows:

```TeX
@inproceedings{heindorf2019debiasing,
  author    = {Stefan Heindorf and
               Yan Scholten and
               Gregor Engels and
               Martin Potthast},
  title     = {Debiasing Vandalism Detection Models at Wikidata},
  booktitle = {{WWW}},
  publisher = {{ACM}},
  year      = {2019}
}
```

Feature Extraction Component
---------------------------------------

### Requirements

The code was tested with Java 8, under Linux 4.9.0-8-amd64 with 16 cores and 256 GB RAM.

We require an installation of `7z` for decompression.

### Installation

We assume the following project structure:
```
www19-fair
├── data
├── www19-fair-feature-classification
└── www19-fair-feature-extraction
```

### Required Data

Before you can start the feature extraction, you need to download the following data:

1. [Wikidata Vandalism Corpus 2016](https://www.wsdm-cup-2017.org/vandalism-detection.html#corpus-wdvc-16):

	Expected Path: `www19-fair/data/external/wdvc-2016/`

2. [Wikidata JSON Dump of 2/29/2016](https://archive.org/download/wikidata-json-20160229/wikidata-20160229-all.json.gz):

	Expected Path: `www19-fair/data/external/wikidata-20160229-all.json.bz2`

3. [WDVD features](https://groups.uni-paderborn.de/wdqa/www19-fair/data/features/):

	Expected Path: `www19-fair/data/features/wdvd_features.csv.bz2`

### Execute 

To start the feature extraction, you need to execute `./run.sh`.

### Computed Features

This feature extraction component will compute the following feature files: 

```
www19-fair/data/
├── features/
│   ├── test/
│   │   ├── embeddings/
│   │   └── features.csv.bz2
│   ├── training/
│   │   ├── embeddings/
│   │   └── features.csv.bz2
│   └── validation/
│       ├── embeddings/
│       └── features.csv.bz2
├── item-properties/
│   └── item-properties.bz2
└── wikidata-graph/
    └── wikidata-graph.csv.bz2
```

**features:** Contains the features for the models FAIR-E and FAIR-S. The file has the following columns: 
`revisionId`, `isEditingTool`, `subject`, `predicate`, `object`, `superSubject`, `superObject`.
Each row was extracted from the Wikidata Vandalism Corpus 2016 and represents a revision that adds, removes, or updates
statements between two Wikidata items.

**embeddings:** This folder contains predicate embeddings as described in the paper. 
We store embeddings in four CSR-matrices: `subjectOut`, `predicate`, `objectOut`, `objectIn`.

**item-properties:** The list of Wikidata item properties extracted from the Wikidata JSON Dump from 2/29/2016. 
Item properties are the Wikidata properties solely used to describe relations between two Wikidata items.

**wikidata-graph:** Statements between two Wikidata items extracted from the Wikidata JSON Dump from 2/29/2016. 
This file contains subject-predicate-object-triple where subject and object are Wikidata items. 
The predicate is an item property.

Contact
-------

For questions and feedback please contact:

Stefan Heindorf, Paderborn University  
Yan Scholten, Paderborn University  
Gregor Engels, Paderborn University  
Martin Potthast, Leipzig University  

License
-------

The code by Stefan Heindorf, Yan Scholten, Gregor Engels, Martin Potthast is licensed under a MIT license.
