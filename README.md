# Data Mining: Association Analysis

In this assignment, we were asked to implement Apriori algorithm that discovers a collection of frequent itemsets from a transaction database.

The first uses the transaction-item representation discussed in class, the second uses a binary representation—Each column represents an item and each row denotes a transaction. If the transaction contains the item, the corresponding value is 1, otherwise it is 0. 

The program is very self explanatory. When executed, it will prompt the user to input either "market" or "gene." After the user does so (with sanitized inputs, of course), the program will then run analysis and the Apriori Algorithm on one of the two sets of data. If you wish to view my code for the Apriori Algorithm, refer to the pdf located in the root directory.

The program was written using Eclipse, and is shared as such. Fair warning to those using it without Eclipse: you may have to delete the "package" declaration.

There is a more detailed synopsis in the .doc file wihtin the project.
