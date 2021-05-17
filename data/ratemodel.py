import os
import re
import random
import json
import numpy as np
from csv import reader
from collections import defaultdict
from keras.preprocessing.text import Tokenizer
from keras_preprocessing.text import tokenizer_from_json
from sklearn.feature_extraction.text import TfidfTransformer

from keras.preprocessing.sequence import pad_sequences
from keras import backend as K
from keras.utils import to_categorical
import tensorflow as tf
from keras.models import Sequential
from keras.layers import Activation,Dense,Dropout, LSTM, Embedding, Conv1D, Masking, Flatten

random.seed(1337)
np.random.seed(1337)
tf.random.set_seed(1337)

vocab_size = 10000

data = list()
cat = list()
qtypes = []
qs = []

categories = [ 'rate:high', 'rate:low', 'accept']
                                    
try:
        datfile = open('rateaccept.tsv', 'r')
        lines = datfile.readlines()
        for entry in lines:
            qtypes.append(entry.split("\t")[0])
            qs.append(entry.split("\t")[1])
        print(qtypes)
        print(qs)
        for qtype, q in zip(qtypes, qs):
                q = re.sub('[^0-9a-zA-Z]+', ' ', q)
                q = q.lower()
                print(q)
                if qtype=='rate:high':
                        data.append(q)                 
                        cat.append(0)
                elif qtype=='rate:low':
                        data.append(q)
                        cat.append(1)
                elif qtype=='accept':
                        data.append(q)
                        cat.append(2)
                    
except UnicodeDecodeError as ude:
    pass

print(cat.count(0))
print(cat.count(1))
print(cat.count(2))

temp = list(zip(cat, data))
random.shuffle(temp)

for entry in temp:
        print(entry)

cat, data = zip(*temp)

#Need to split now, so not to oversample test set
trainlen = int(len(cat) * 1.00)
vallen = int(len(cat) * 0.00)
testlen = int(len(cat) * 0.00)

traindat = data[:trainlen]
#valdat = traindat
valdat = data[trainlen:trainlen+vallen]
testdat = data[trainlen+vallen:]

traincat = cat[:trainlen]
#valcat = traincat
valcat = cat[trainlen:trainlen+vallen]
testcat = cat[trainlen+vallen:]

tokenizer = Tokenizer(lower=False, num_words=vocab_size, oov_token="UNK")

#json_tok = open('abnb_pets_tok.json', 'r')
#loaded_tok = json_tok.read()
#json_tok.close()
#tokenizer = tokenizer_from_json(loaded_tok)

tokenizer.fit_on_texts(traindat)

tokenizer_json = tokenizer.to_json()
with open('rate_tok.json', 'w', encoding='utf-8') as f:
        f.write(tokenizer_json)

##Xtrain = tokenizer.texts_to_matrix(os_traindat, mode='freq')
Ytrain = np.asarray(traincat)

Yval = np.asarray(valcat)

#Xtest = tokenizer.texts_to_matrix(testdat, mode='freq')
Ytest = np.asarray(testcat)

Xtrain = tokenizer.texts_to_sequences(traindat)
Xval = tokenizer.texts_to_sequences(valdat)
Xtest = tokenizer.texts_to_sequences(testdat)

text_maxlen = 50
num_classes = 3
vector_size = 100
batch_size = 100

Xtrain = pad_sequences(Xtrain, padding="post", truncating="post", maxlen= text_maxlen)
Xval = pad_sequences(Xval, padding="post", truncating="post", maxlen= text_maxlen)
Xtest = pad_sequences(Xtest, padding="post", truncating="post", maxlen= text_maxlen)

Ytrain = to_categorical(Ytrain, num_classes=num_classes)
Yval = to_categorical(Yval, num_classes=num_classes)
Ytest = to_categorical(Ytest, num_classes=num_classes)

model = Sequential()

embeddings_index = dict()
f = open('glove.6B.100d.txt')
for line in f:
        values = line.split()
        word = values[0]
        coefs = np.asarray(values[1:], dtype='float32')
        embeddings_index[word]= coefs
f.close()

embedding_matrix = np.zeros((vocab_size, 100))
for word, i in tokenizer.word_index.items():
        embedding_vector = embeddings_index.get(word)
        if embedding_vector is not None:
                embedding_matrix[i] = embedding_vector

model.add(Embedding(vocab_size, 100, weights =[embedding_matrix], input_length=text_maxlen, trainable=False))
model.add(LSTM(vector_size, return_sequences=True))
model.add(Flatten())
model.add(Dense(num_classes, activation='softmax'))

model.summary()

model.compile(loss='categorical_crossentropy', 
                optimizer='adam', 
                metrics=['accuracy'])

K.set_value(model.optimizer.learning_rate, 0.001)

history = model.fit(Xtrain, Ytrain,
                    batch_size=batch_size,
                    epochs=10,
                    verbose=1,
                    validation_data=(Xval, Yval))

model.save('rate_model.h5')
model.summary()

#Ypred = model.predict(Xtest)


#Ypred = np.argmax(Ypred, axis=1)
#Ytest = np.argmax(Ytest, axis=1)

from sklearn import metrics
#class_out = open("eval_report.txt", "w")
#class_out.write(metrics.classification_report(Ytest, Ypred, labels=[0,1,2,3,4,5,6,7,8,9,10,11,12], target_names=categories))
#class_out.close()

#print(metrics.classification_report(Ytest, Ypred, labels=[0,1,2,3,4,5,6,7,8,9,10,11,12], target_names=categories))
#cm = metrics.confusion_matrix(Ytest, Ypred).transpose()

#np.set_printoptions(linewidth=500)
#np.set_printoptions(threshold=np.inf)

#print(cm)

#conf_out = open("eval_confusionmatrix.txt", "w")
#conf_out.write(str(cm))
#conf_out.close()
