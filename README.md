# IR2
Information Storage and Retrieval course project phase 2:
This program reads an input file of multiple documents separated with \<p>, tokenizes them, stems the tokens using Paice/Husk algorithm, and creates a dictionary of the results. The dictionary is constructed using Ternary Search Tree which is implemented with a data structure called <a href="https://github.com/makantayebi/SimpleDSTools/tree/master/src">Train</a>. The TST was implemented using different data structers, Train was the best in both memory and time.
<p>The user can type in a query to search in the document set. The program merges the posting lists of the words to find the matching documents for the input vector. The output is the index of the documents which contain all of the queries tokens.
