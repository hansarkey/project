import os
import re
import random
import numpy as np
import tensorflow as tf
import json
from collections import defaultdict
from keras.preprocessing.text import Tokenizer
from sklearn.feature_extraction.text import TfidfTransformer

from keras.preprocessing.sequence import pad_sequences
from keras.preprocessing.text import tokenizer_from_json
from keras import backend as K
from keras.utils import to_categorical
from keras.models import Sequential
from keras.layers import Activation, Dense, Dropout, LSTM, Embedding, Conv1D, Masking, Flatten


random.seed(1337)
np.random.seed(1337)
tf.random.set_seed(1337)

vocab_size = 10000

#categories = [ os.path.basename(f.path) for f in os.scandir('data/') if f.is_file() ]
# convert list comp to set to get only unique categories, then list again for type purposes
categories = list(set([line.split(':')[0] for line in open('cuisine_samples.txt').readlines()]))
#print(categories)

dat = list()
cat = list()

#for i, category in enumerate(categories):
#samples = [ f.path for f in os.scandir('data') if f.is_file() ]

# Stubs per category -- they should equal each other. 4 in our case
stubsPerCat = int(len(open('cuisine_samples.txt').readlines()) / len(categories))


counter = 0
i = 0
datfile = open('cuisine_samples.txt', mode ='r')
for line in datfile:
    line = line.split(':')[1]
    line = line.replace('\n', ' ')
    line = re.sub('[^0-9a-zA-Z]+', ' ', line.strip())
    line = line.lower()

    # trying to fix problems with i think small data
    #for n in range(0,2):
    #  dat.append(line)
    #  cat.append(i)
    dat.append(line)
    cat.append(i)

    # Increment which category we are on
    counter += 1
    if counter == stubsPerCat:
        counter = 0
        i += 1

    
temp = list(zip(cat, dat))
random.shuffle(temp)
cat, dat = zip(*temp)

# Development Config
'''
trainlen = int(len(cat) * 0.80)
vallen = int(len(cat) * 0.10)
testlen = int(len(cat) * 0.10)
'''
# Deployment Config

trainlen = int(len(cat) * 1.00)
vallen = int(len(cat) * 0.00)
testlen = int(len(cat) * 0.00)

print(trainlen, vallen, testlen)

# was told I could use the training set as validation and test since its a small dataset
traindat = dat[:trainlen]
#valdat = dat[trainlen:trainlen+vallen]
#testdat = dat[trainlen+vallen:]
valdat = traindat
testdat = traindat

traincat = cat[:trainlen]
#valcat = cat[trainlen:trainlen+vallen]
#testcat = cat[trainlen+vallen:]
valcat = traincat
testcat = traincat


tokenizer = Tokenizer(lower=False, num_words=vocab_size, oov_token="UNK")
tokenizer.fit_on_texts(traindat)
#with open("data/abnb_pets_tok.json") as f:
#    data = json.load(f)
#    tokenizer = tokenizer_from_json(json.dumps(data))

# deployment config
tokenizer_json = tokenizer.to_json()

# This is not working correctly it seems
with open("cuisine_tok.json", "w", encoding="utf-8" ) as f:
    f.write(tokenizer_json)

#Xtrain = tokenizer.texts_to_matrix(traindat, mode='freq')
Ytrain = np.asarray(traincat)

Yval = np.asarray(valcat)

#Xtest = tokenizer.texts_to_matrix(testdat, mode='freq')
Ytest = np.asarray(testcat)


Xtrain = tokenizer.texts_to_sequences(traindat)
Xval = tokenizer.texts_to_sequences(valdat)
Xtest = tokenizer.texts_to_sequences(testdat)

text_maxlen = 200
num_classes = len(categories)
vector_size = 100
batch_size = 100

Xtrain = pad_sequences(Xtrain, padding="post", truncating="post", maxlen=text_maxlen)
Xval = pad_sequences(Xval, padding="post", truncating="post", maxlen=text_maxlen)
Xtest = pad_sequences(Xtest, padding="post", truncating="post", maxlen=text_maxlen)

print(Xtest)

Ytrain = to_categorical(Ytrain, num_classes=num_classes)
Yval = to_categorical(Yval, num_classes=num_classes)
Ytest = to_categorical(Ytest, num_classes=num_classes)


# Prepare embedding layer
word_index = tokenizer.word_index

embeddings_index = {}
with open('glove.6B.100d.txt') as f:
    for line in f:
        values = line.split()
        word = values[0]
        coefs = np.asarray(values[1:], dtype='float32')
        embeddings_index[word] = coefs

embedding_matrix = np.zeros((len(word_index)+1, vector_size))
for word, i in word_index.items():
    embedding_vector = embeddings_index.get(word)
    if embedding_vector is not None:
        embedding_matrix[i] = embedding_vector

embedding_layer = Embedding(len(word_index)+1, vector_size, weights=[embedding_matrix], input_length=text_maxlen, trainable=False)

model = Sequential()
#model.add(Embedding(vocab_size, vector_size, input_length=text_maxlen))
model.add(embedding_layer)
model.add(LSTM(vector_size, return_sequences=True))
model.add(Flatten())
model.add(Dense(num_classes, activation='softmax'))

model.summary()

model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

K.set_value(model.optimizer.learning_rate, 0.001)

history = model.fit(Xtrain, Ytrain, batch_size=batch_size, epochs=30, verbose=1, validation_data=(Xval, Yval))

# deployment config

model.save("cuisines.h5")


Ypred = model.predict(Xtest)

Ypred = np.argmax(Ypred, axis=1)
Ytest = np.argmax(Ytest, axis=1)

from sklearn import metrics

# development config
with open('eval_report.txt', 'w') as f:
    f.write(metrics.classification_report(Ytest, Ypred, target_names=categories, labels=np.arange(len(categories))) )

print(metrics.classification_report(Ytest, Ypred, target_names=categories, labels=np.arange(len(categories))))

cm = metrics.confusion_matrix(Ytest, Ypred).transpose()

np.set_printoptions(linewidth=500)
np.set_printoptions(threshold=np.inf)

# development config
with open('eval_confusionmatrix.txt', 'w') as f:
    print(cm, file=f)
print(cm)

