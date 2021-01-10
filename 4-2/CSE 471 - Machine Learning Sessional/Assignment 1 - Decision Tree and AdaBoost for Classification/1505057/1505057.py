import numpy as np
import pandas as pd
from pprint import pprint
from sklearn.preprocessing import Binarizer 
from sklearn.metrics import confusion_matrix
from sklearn.model_selection import train_test_split
from bisect import bisect_left
import random

random.seed(0)

def entropy(p):
    if p == 1 or p == 0:
        return 0
    return -p * np.log2(p) - (1 - p) * np.log2(1 - p)


def infoGain(split, target):
    total = len(target)
    positive = sum(target == True)
    totalEntropy = entropy(positive / total)
    wieghtedEntropy = 0
    for value in np.sort(np.unique(split)):
        sTarget = target[split == value]
        positive = sum(sTarget == True)
        negative = len(sTarget) - positive
        wieghtedEntropy += ((positive + negative) / total) * entropy(positive / (positive + negative))
    return totalEntropy - wieghtedEntropy


def getSplit(data, target):
    df = pd.DataFrame({'data': data, 'target': target})
    df = df.sort_values('data')
    data = df.to_numpy()
    x = data[:, 0]
    y = data[:, 1]
    pos = sum(y == True)
    neg = len(x) - pos
    posLeft = 0
    negLeft = 0
    parentEntropy = entropy(pos / (pos + neg))
    maxGain = 0
    value = None
    for i in range(pos + neg):
        if y[i] == True:
            posLeft += 1
        else:
            negLeft += 1
        if x[i] == x[pos + neg - 1]:
            break
        if i < pos + neg - 1 and x[i] == x[i + 1]:
            continue
        posRight = pos - posLeft
        negRight = neg - negLeft
        gain = parentEntropy - ((posLeft + negLeft) / (pos + neg)) * entropy(posLeft / (posLeft + negLeft)) - (
                    (posRight + negRight) / (pos + neg)) * entropy(posRight / (posRight + negRight))
        if gain > maxGain:
            maxGain = gain
            value = x[i + 1]
    return value


def decisionTree(data, features, parentData, depth, target):
    if (depth == 0):
        return np.unique(data[target])[np.argmax(np.unique(data[target], return_counts=True)[1])]
    if len(data) == 0:
        return np.unique(parentData[target])[
            np.argmax(np.unique(parentData[target], return_counts=True)[1])]
    elif len(np.unique(data[target])) == 1:
        return np.unique(data[target])[0]
    elif len(features) == 0:
        return np.unique(data[target])[np.argmax(np.unique(data[target], return_counts=True)[1])]
    else:
        infoGains = [infoGain(data[feature], data[target]) for feature in features]
        bestFeature = features[np.argmax(infoGains)]
        tree = {bestFeature: {}}
        features = [i for i in features if i != bestFeature]
        for value in np.unique(data[bestFeature]):
            subData = data.where(data[bestFeature] == value).dropna()
            subTree = decisionTree(subData, features, data, depth - 1, target)
            tree[bestFeature][value] = subTree
        return (tree)


def predict(query, tree, default=True):
    for key in list(query.keys()):
        if key in list(tree.keys()):
            try:
                result = tree[key][query[key]]
            except:
                return default
            if isinstance(result, dict):
                return predict(query, result)
            else:
                return result


def test(data, tree, target):
    queries = data.iloc[:, :-1].to_dict(orient="records")
    predicted = pd.DataFrame(columns=["predicted"])
    maxValue = np.unique(data[target])[np.argmax(np.unique(data[target],return_counts=True)[1])]
    for i in range(len(data)):
        temp = predict(queries[i], tree, 1.0)
        predicted.loc[i, "predicted"] = predict(queries[i], tree, maxValue)
    TN, FP, FN, TP = confusion_matrix(data[target], predicted['predicted'].astype('bool')).ravel()
    accuracy = (TP + TN) / (TN + FP + FN + TP) * 100
    print(f'Accuracy : {accuracy}')
    recall = TP / (TP + FN) * 100  # true positive rate
    print(f'Recall : {recall}')
    specificity = TN / (TN + FP) * 100  # true negative rate
    print(f'Specificity : {specificity}')
    precision = TP / (TP + FP) * 100
    print(f'Precision : {precision}')
    falseDiscoryRate = FP / (TP + FP) * 100
    print(f'False Discovery Rate : {falseDiscoryRate}')
    f1score = (2 * precision * recall) / (precision + recall)
    print(f'F1 Score : {f1score}')


def resample(data, w):
    dataCopy = pd.DataFrame(index=data.index, columns=data.columns)
    cw = []
    for i in range(len(w)):
        if i == 0:
            cw.append(w[i]);
        else:
            cw.append(cw[i - 1] + w[i]);
    for i in range(len(cw)):
        index = bisect_left(cw, random.random())
        dataCopy.iloc[i] = data.iloc[index];
    return dataCopy


def adaboost(data, K, target):
    n = len(data)
    w = np.array([1 / n] * n)
    h = []
    z = np.array([0.0] * K)
    for k in range(K):
        data = resample(data, w)
        h.append(decisionTree(data, data.columns[:-1], data, 1, target))
        error = 0
        queries = data.iloc[:, :-1].to_dict(orient="records")
        for j in range(n):
            if (predict(queries[j], h[k]) != data[target][j]):
                error = error + w[j]
        if (error > 0.5):
            continue
        if error == 0:
            error += .00000001
        for j in range(n):
            if (predict(queries[j], h[k]) == data[target][j]):
                w[j] = w[j] * error / (1 - error)
        s = sum(w)
        w = w / s
        z[k] = np.log((1 - error) / error)
    return h, z


def testAda(data, h, z, target):
    queries = data.iloc[:, :-1].to_dict(orient="records")
    predicted = pd.DataFrame(columns=["predicted"])
    for i in range(len(queries)):
        yes = no = 0
        for k in range(len(h)):
            res = (predict(queries[i], h[k]))
            if res == True:
                yes += z[k]
            else:
                no += z[k]
        if (yes > no):
            predicted.loc[i, "predicted"] = True
        else:
            predicted.loc[i, "predicted"] = False
    TN, FP, FN, TP = confusion_matrix(data[target], predicted['predicted'].astype(bool)).ravel()
    accuracy = (TP + TN) / (TN + FP + FN + TP) * 100
    print(f'K = {len(h)}\n Accuracy : {accuracy}')





#telco 
dataset = pd.read_csv('data/WA_Fn-UseC_-Telco-Customer-Churn.csv')

# data overview
print ("Rows     : " ,dataset.shape[0])
print ("Columns  : " ,dataset.shape[1])
print ("\nFeatures : \n" ,dataset.columns.tolist())
print ("\nMissing values :  ", dataset.isnull().sum().values.sum())
print ("\nUnique values :  \n",dataset.nunique())
print(dataset.shape)

dataset["TotalCharges"] = dataset["TotalCharges"].apply (pd.to_numeric, errors='coerce')
dataset["MonthlyCharges"]=dataset["MonthlyCharges"].apply (pd.to_numeric, errors='coerce')
dataset["tenure"]=dataset["tenure"].apply (pd.to_numeric, errors='coerce')
dataset=dataset.drop('customerID',axis=1)
dataset=dataset.dropna()
dataset.reset_index(drop=True, inplace=True)

#preprocessing
'''
replace_cols = [ 'OnlineSecurity', 'OnlineBackup', 'DeviceProtection',
                'TechSupport','StreamingTV', 'StreamingMovies']
for i in replace_cols : 
    dataset[i]  = dataset[i].replace({'No internet service' : 'No'})
'''

#replace target value
dataset['Churn'] = dataset["Churn"].replace({'Yes':True,'No':False})
dataset['Churn'] = dataset["Churn"].astype('bool')

#binarize
for col in range(len(dataset.columns)):
    colName = dataset.columns[col]
    if(dataset[colName].nunique()>50):
        print(f'preprocessing {colName}')
        originalFeature=np.array(dataset[colName]).reshape(1,-1)
        dataset[colName]=Binarizer(getSplit(dataset[colName],dataset['Churn'])).fit_transform(originalFeature).flatten()

dataset.to_csv('data/Telco_Preprocessed.csv',index=False)
'''
dataset = pd.read_csv('data/Telco_Preprocessed.csv')
print(dataset)
dataset=dataset.dropna()
dataset.reset_index(drop=True, inplace=True)
'''

# splitting the data
training, testing = train_test_split(dataset, test_size=0.2, random_state=3)

#fitting and testing
tree = decisionTree(training,training.columns[:-1],training,7,'Churn')
#pprint(tree)
print('Tesiting results:')
test(testing.reset_index(drop=True),tree,'Churn')
print('Training results:')
test(training.reset_index(drop=True),tree,'Churn')

#adaboost
for r in range(5,21,5):
    h,z = adaboost(training.reset_index(drop=True), r, 'Churn')
    print('Testing results:')
    testAda(testing.reset_index(drop=True), h, z, 'Churn')
    print('Training results:')
    testAda(training.reset_index(drop=True), h, z, 'Churn')






#adult 
dataset = pd.read_csv("data/adult.csv")
dataset.columns = ['age', 'workclass', 'fnlwgt', 'education', 'education-num', 'marital-status', 'occupation', 
               'relationship', 'race', 'sex', 'capital-gain', 'capital-loss', 'hours-per-week', 'native-country', 
                'salary-greater-50k']
# data overview
print ("Rows     : " ,dataset.shape[0])
print ("Columns  : " ,dataset.shape[1])
print ("\nFeatures : \n" ,dataset.columns.tolist())
print ("\nMissing values :  ", dataset.isnull().sum().values.sum())
print ("\nUnique values :  \n",dataset.nunique())
print(dataset.shape)
#print(dataset.head())

dataset["age"] = dataset["age"].apply(pd.to_numeric, errors='coerce')
dataset["fnlwgt"] = dataset["fnlwgt"].apply(pd.to_numeric, errors='coerce')
dataset["education-num"]=dataset["education-num"].apply(pd.to_numeric, errors='coerce')
dataset["capital-gain"]=dataset["capital-gain"].apply(pd.to_numeric, errors='coerce')
dataset["capital-loss"]=dataset["capital-gain"].apply(pd.to_numeric, errors='coerce')
dataset=dataset.dropna()
dataset.reset_index(drop=True, inplace=True)

#replace '?' in workclass and native country
workclassMax = np.unique(dataset['workclass'])[np.argmax(np.unique(dataset['workclass'],return_counts=True)[1])]
dataset['workclass'] = dataset['workclass'].replace(' ?',workclassMax)
nativeMax = np.unique(dataset['native-country'])[np.argmax(np.unique(dataset['native-country'],return_counts=True)[1])]
dataset['native-country'] = dataset['native-country'].replace(' ?',nativeMax)

#replace target value
dataset['salary-greater-50k'] = dataset["salary-greater-50k"].replace({' >50K':True,' <=50K':False})
dataset['salary-greater-50k'] = dataset["salary-greater-50k"].astype('bool')

#print(dataset.head())

#binarize
for col in range(len(dataset.columns)):
    colName = dataset.columns[col]
    if(dataset[colName].nunique()>50):
        print(f'preprocessing {colName}')
        originalFeature=np.array(dataset[colName]).reshape(1,-1)
        dataset[colName]=Binarizer(getSplit(dataset[colName],dataset['salary-greater-50k'])).fit_transform(originalFeature).flatten()
        
#print(dataset.head())

dataset.to_csv('data/Adult_Preprocessed.csv',index=False)

# splitting the data
training, testing = train_test_split(dataset, test_size=0.2, random_state=3)

#fitting and testing
tree = decisionTree(training,training.columns[:-1],training,8,'salary-greater-50k')
#pprint(tree)
print('Tesiting results:')
test(testing.reset_index(drop=True),tree, 'salary-greater-50k')
print('Training results:')
test(training.reset_index(drop=True),tree, 'salary-greater-50k')

#adaboost
for r in range(5,21,5):
    h,z = adaboost(training.reset_index(drop=True), r, 'salary-greater-50k')
    print('Testing results:')
    testAda(testing.reset_index(drop=True), h, z, 'salary-greater-50k')
    print('Training results:')
    testAda(training.reset_index(drop=True), h, z, 'salary-greater-50k')






#credit
dataset = pd.read_csv("data/creditcard.csv")

# data overview
print ("Rows     : " ,dataset.shape[0])
print ("Columns  : " ,dataset.shape[1])
print ("\nFeatures : \n" ,dataset.columns.tolist())
print ("\nMissing values :  ", dataset.isnull().sum().values.sum())
print ("\nUnique values :  \n",dataset.nunique())
print(dataset.shape)
#print(dataset.head())

dataset.sort_values(by=['Class'], inplace=True, ascending=False)
#print(dataset.head(100))
print(np.unique(dataset['Class'],return_counts=True)[1])
dataset=dataset.head(20000)
dataset=dataset.sample(frac=1).reset_index(drop=True)
print(dataset.head())
print(np.unique(dataset['Class'],return_counts=True)[1])
dataset=dataset.dropna()
dataset.reset_index(drop=True, inplace=True)

#replace target value
print(np.unique(dataset['Class']))
dataset['Class'] = dataset['Class'].replace({1:True,0:False})
dataset['Class'] = dataset['Class'].astype('bool')

#print(dataset.head())

#binarize
for col in range(len(dataset.columns)):
    colName = dataset.columns[col]
    if(dataset[colName].nunique()>50):
        print(f'preprocessing {colName}')
        originalFeature=np.array(dataset[colName]).reshape(1,-1)
        dataset[colName]=Binarizer(getSplit(dataset[colName],dataset['Class'])).fit_transform(originalFeature).flatten()
        
#print(dataset.head())

dataset.to_csv('data/Credit_Preprocessed.csv',index=False)

# splitting the data
training, testing = train_test_split(dataset, test_size=0.2, random_state=3)

#fitting and testing
tree = decisionTree(training,training.columns[:-1],training,8,'Class')
#pprint(tree)
test(testing.reset_index(drop=True),tree, 'Class')
test(training.reset_index(drop=True),tree, 'Class')

#adaboost
for r in range(5,21,5):
    h,z = adaboost(training.reset_index(drop=True), r, 'Class')
    print('Testing results:')
    testAda(testing.reset_index(drop=True), h, z, 'Class')
    print('Training results:')
    testAda(training.reset_index(drop=True), h, z, 'Class')

