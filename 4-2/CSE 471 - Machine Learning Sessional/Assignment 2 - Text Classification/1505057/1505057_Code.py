# To add a new cell, type '# %%'
# To add a new markdown cell, type '# %% [markdown]'
# %%
import os
import re
import string
import nltk
import pandas as pd
import numpy as np
from bs4 import BeautifulSoup
from tqdm.notebook import tqdm
from collections import Counter
from nltk.stem import WordNetLemmatizer
from nltk.corpus import stopwords
import unicodedata
from prettytable import PrettyTable
from scipy import stats


# %%
nltk.download('punkt')
nltk.download('wordnet')
nltk.download('stopwords')


# %%
def preprocess_text(text):
    # stopwords without punctuation
    stop_words = [word.replace('\'', '')
                  for word in stopwords.words('english')]
    # remove non ascii
    text = unicodedata.normalize('NFKD', text).encode(
        'ascii', 'ignore').decode('utf-8', 'ignore')
    text = text.lower()
    text = re.sub(r'(<.*?>)', '', text)  # removing html markup
    text = re.sub(r'[^\w\s]', '', text)  # removing punctuation
    text = re.sub(r'[\d]', '', text)  # removing digits
    # remove stopwords
    tokenized = []
    for word in text.split():
        if word not in stop_words:
            tokenized.append(word)
    for i in range(len(tokenized)):
        word = tokenized[i]
        word = WordNetLemmatizer().lemmatize(word, pos='v')
        tokenized[i] = word
    return ' '.join(tokenized)


# %%
def create_df(directory):
    data = []
    for filename in os.listdir(directory):
        filepath = os.path.join(directory, filename)
        topic = filename.split('.')[0].lower()
        i = 0
        print('Topic: ', topic)
        with open(filepath, 'r', encoding='UTF-8') as file:
            soup = BeautifulSoup(file.read(), features='html.parser')
            for items in tqdm(soup.findAll("row"), total=1200):
                document = items['body']
                if len(document) <= 1:
                    continue
                temp = dict()
                temp['raw'] = document
                temp['x'] = preprocess_text(document)
                temp['y'] = topic
                if i < 500:
                    temp['split'] = 'train'
                elif i < 700:
                    temp['split'] = 'validation'
                else:
                    temp['split'] = 'test' + str(int((i-700)/10))
                data.append(temp)
                i += 1
                if i == 1200:
                    break
        print('Total size: ', len(data))
    return pd.DataFrame(data)


# %%
df = create_df('./Training')
df.to_csv('data.csv')


# %%
df = pd.read_csv("data.csv")
print(df.shape)
df.head()


# %%
def calculate_dist(a, b, metric='hamming'):
    assert a.shape == b.shape, 'vector shapes need to be equal'
    if metric == 'hamming':
        return np.count_nonzero(a != b)
    elif metric == 'euclidean':
        return np.linalg.norm(a - b)
    elif metric == 'cosine':
        return -np.dot(a, b)/(np.linalg.norm(a)*np.linalg.norm(b))


# %%
def create_dictionary(data):
    dictionary = dict()
    counter = Counter()
    for document in data:
        counter.update(document.split())
    for i, item in enumerate(counter.most_common()):
        dictionary[item[0]] = i
    print('Word dictionary size : ', len(counter))
    return dictionary


# %%
class KNN_Classifier():
    def __init__(self, k, train, validation, metric='hamming'):
        self.k = k
        train.reset_index(inplace=True)
        validation.reset_index(inplace=True)
        self.train_x = train['x']
        self.train_y = train['y']
        self.validation_x = validation['x']
        self.validation_y = validation['y']
        self.metric = metric
        print('Training Example: ', len(self.train_x))
        print('Validation Example: ', len(self.validation_x))

    def hamming(self, document):
        vector = np.zeros(len(self.dictionary))
        for word in document.split():
            if word in self.dictionary:
                vector[self.dictionary[word]] = 1
        return vector

    def hamming_vector(self):
        self.vector = np.zeros((len(self.train_x), len(self.dictionary)))
        for i in range(len(self.train_x)):
            self.vector[i] = self.hamming(self.train_x[i])

    def euclidean(self, document):
        vector = np.zeros(len(self.dictionary))
        for word in document.split():
            if word in self.dictionary:
                vector[self.dictionary[word]] += 1
        return vector

    def euclidean_vector(self):
        self.vector = np.zeros((len(self.train_x), len(self.dictionary)))
        for i in range(len(self.train_x)):
            self.vector[i] = self.euclidean(self.train_x[i])

    def idf(self):
        N = len(self.train_x)
        alpha, beta = 0, 0
        idf = dict()
        counter = Counter()
        for document in self.train_x:
            counter.update(list(set(document.split())))
        for word, frequency in counter.most_common():
            if word in self.dictionary:
                idf[word] = max(0.00001, np.log(
                    (alpha + N)/(beta + frequency)))
        return idf

    def cosine(self, document):
        vector = np.zeros(len(self.dictionary))
        counter = Counter()
        counter.update(document.split())
        for word, frequency in counter.most_common():
            if word in self.dictionary:
                vector[self.dictionary[word]] += frequency*self.idf[word]
        return vector

    def cosine_vector(self):
        self.idf = self.idf()
        self.vector = np.zeros((len(self.train_x), len(self.dictionary)))
        for i in range(len(self.train_x)):
            self.vector[i] = self.cosine(self.train_x[i])

    def fit(self):
        self.dictionary = create_dictionary(self.train_x)
        if self.metric == 'hamming':
            self.hamming_vector()
        elif self.metric == 'euclidean':
            self.euclidean_vector()
        elif self.metric == 'cosine':
            self.cosine_vector()

    def predict(self, x):
        N = self.vector.shape[0]
        dist = np.zeros(N)
        for i in range(N):
            if self.metric == 'hamming':
                dist[i] = calculate_dist(self.vector[i], x, metric='hamming')
            elif self.metric == 'euclidean':
                dist[i] = calculate_dist(self.vector[i], x, metric='euclidean')
            elif self.metric == 'cosine':
                dist[i] = calculate_dist(self.vector[i], x, metric='cosine')
        top_k = np.argsort(dist)[:self.k]
        res = [self.train_y[i] for i in top_k]
        counter = Counter()
        counter.update(res)
        return counter.most_common(1)[0][0]

    def validation_accuracy(self):
        N = len(self.validation_x)
        correct = 0
        for i in tqdm(range(N), total=N):
            document = self.validation_x[i]
            if self.metric == 'hamming':
                vector = self.hamming(document)
            elif self.metric == 'euclidean':
                vector = self.euclidean(document)
            elif self.metric == 'cosine':
                vector = self.cosine(document)
            prediction = self.predict(vector)
            if prediction == self.validation_y[i]:
                correct += 1
        return correct/N*100.0

    def test_accuracy(self, test):
        test.reset_index(inplace=True)
        test_x, test_y = test['x'], test['y']
        N = len(test_x)
        print('Test Example: ', N)
        correct = 0
        for i in tqdm(range(N), total=N):
            document = test_x[i]
            if self.metric == 'hamming':
                vector = self.hamming(document)
            elif self.metric == 'euclidian':
                vector = self.euclidean(document)
            elif self.metric == 'cosine':
                vector = self.cosine(document)
            prediction = self.predict(vector)
            if prediction == test_y[i]:
                correct += 1
        return correct/N*100.0


# %%
print('*'*50 + ' Validating KNN ' + '*'*50)
METRIC = ['hamming', 'euclidean', 'cosine']
K = [1, 3, 5]
table = PrettyTable()
table.field_names = ["k", "1", "3", "5"]
table.float_format = '0.3'
best_knn_model = None
best_accuracy = 0
for metric in METRIC:
    temp = [metric]
    for k in K:
        print(f'KNN Validation on k={k} and metric={metric}')
        knn = KNN_Classifier(k=k, train=df[df['split'] == 'train'],
                             validation=df[df['split'] == 'validation'], metric=metric)
        knn.fit()
        accuracy = knn.validation_accuracy()
        temp.append(round(accuracy, 3))
        if accuracy > best_accuracy:
            best_accuracy = accuracy
            best_knn_model = knn
        print(
            f'Validation accuracy of KNN for k={k} and metric={metric}: {accuracy}')
    table.add_row(temp)
print(table)


# %%
knn_accuracy = []
print('*'*50 + ' Testing KNN ' + '*'*50)
for i in range(50):
    temp = 'test' + str(i)
    test = df[df['split'] == temp]
    accuracy = best_knn_model.test_accuracy(test)
    print(f'KNN accuracy on test {i+1}: {accuracy:.3f}')
    knn_accuracy.append(accuracy)


# %%
class NB_Classifier():
    def __init__(self, train, validation, smoothing_factor=1):
        train.reset_index(inplace=True)
        validation.reset_index(inplace=True)
        self.train = train
        self.validation = validation
        self.smoothing_factor = smoothing_factor
        self.topics = self.train['y'].unique()
        print('Training Example: ', len(self.train))
        print('Validation Example: ', len(self.validation))

    def fit(self):
        self.class_document_cnt = {}
        self.words_in_class_counter = {}
        self.total_words_in_class_cnt = {}
        total_counter = Counter()
        for topic in self.topics:
            total = 0
            counter = Counter()
            data = self.train[self.train['y'] == topic]
            self.class_document_cnt[topic] = len(data)
            for document in data['x']:
                tokens = document.split()
                total += len(tokens)
                counter.update(tokens)
                total_counter.update(tokens)
            self.total_words_in_class_cnt[topic] = total
            self.words_in_class_counter[topic] = counter
        self.V = len(total_counter)

    def predict(self, x):
        probability = np.zeros(len(self.topics))
        for i, topic in enumerate(self.topics):
            N = self.total_words_in_class_cnt[topic]
            probability[i] += np.log(self.class_document_cnt[topic] /
                                     len(self.train))
            for word in x.split():
                word_cnt = self.words_in_class_counter[topic][word]
                probability[i] += np.log((word_cnt + self.smoothing_factor) /
                                         (N + self.smoothing_factor * self.V))
        return self.topics[np.argsort(probability)[-1]]

    def validation_accuracy(self):
        N = len(self.validation)
        correct = 0
        for i in tqdm(range(N)):
            document = self.validation['x'][i]
            prediction = self.predict(document)
            if prediction == self.validation['y'][i]:
                correct += 1
        return correct/N*100.0

    def test_accuracy(self, test):
        test.reset_index(inplace=True)
        N = len(test)
        print('Test Example: ', N)
        correct = 0
        for i in tqdm(range(N)):
            document = test['x'][i]
            prediction = self.predict(document)
            if prediction == test['y'][i]:
                correct += 1
        return correct/N*100.0


# %%
print('*'*50 + ' Validating NB ' + '*'*50)
smoothing_factors = [0.05, 0.1, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6]
table = PrettyTable()
table.field_names = ['Smoothing Factor', 'Accuracy']
table.float_format = '0.3'
best_nb_model = None
best_accuracy = 0
for sf in smoothing_factors:
    print("NB Validation on smoothing factor=", sf)
    nb = NB_Classifier(train=df[df['split'] == 'train'],
                       validation=df[df['split'] == 'validation'], smoothing_factor=sf)
    nb.fit()
    accuracy = nb.validation_accuracy()
    table.add_row([round(sf, 2), round(accuracy, 3)])
    if accuracy > best_accuracy:
        best_accuracy = accuracy
        best_nb_model = nb
    print(f'Validation accuracy of NB for smoothing factor={sf}: {accuracy}')
print(table)


# %%
nb_accuracy = []
print('*'*50 + ' Testing NB ' + '*'*50)
for i in range(50):
    temp = 'test' + str(i)
    test = df[df['split'] == temp]
    accuracy = best_nb_model.test_accuracy(test)
    print(f'NB accuracy on test {i+1}: {accuracy:.3f}')
    nb_accuracy.append(accuracy)


# %%
table = PrettyTable()
table.field_names = ['Test Number', 'KNN Accuracy', 'NB Accuracy']
table.float_format = '0.3'
for i in range(50):
    table.add_row([i+1, knn_accuracy[i], nb_accuracy[i]])
print(table)


# %%
statistic, pvalue = stats.ttest_ind(nb_accuracy, knn_accuracy)
print("T Test Score (nb, knn): ", statistic)
print("P value (nb, knn): ", pvalue)
