# To add a new cell, type '# %%'
# To add a new markdown cell, type '# %% [markdown]'
# %%
import pandas as pd
import numpy as np
from sklearn.preprocessing import StandardScaler
import matplotlib.pyplot as plt


# %%
df = pd.read_csv('data.txt', sep = ' ', header = None)
X = df.to_numpy()
print(X.shape)
df.head()


# %%
class PCA():
    def __init__(self, n_components):
        self.n_components = n_components

    def fit_transform(self, X):
        # X = StandardScaler().fit_transform(X)
        N = X.shape[0]
        mean = np.mean(X, axis=0)
        temp = X - mean 
        C = np.dot(np.transpose(temp),temp)/N
        e_vals, e_vecs = np.linalg.eig(C)
        e_pairs = [(np.abs(e_vals[i]), e_vecs[:,i]) for i in range(len(e_vals))]
        e_pairs.sort(key=lambda x: x[0], reverse=True)
        W = np.hstack((e_pairs[0][1].reshape(-1,1),
                      e_pairs[1][1].reshape(-1,1)))
        return np.dot(X,W)


# %%
pca = PCA(n_components = 2)
principal_components = pca.fit_transform(X)
print(principal_components.shape)
plt.title('2 Component PCA') 
plt.xlabel('PC1') 
plt.ylabel('PC2') 
plt.scatter(principal_components[:,0],principal_components[:,1],edgecolors='b', facecolors = 'none')
plt.gca().set_aspect('equal', adjustable='box')
plt.savefig('pca.png', dpi=500)
plt.show()


# %%
class GMM():
    def __init__(self, n_components, max_iter=100):
        self.n_components = n_components
        self.max_iter = max_iter
        np.random.seed(7)

    def get_weighted_normal_scores(self, X):
        N, D = X.shape
        normal_scores = np.zeros((N, self.n_components))
        for k in range(self.n_components):
            for i in range(N):
                temp = X[i] - self.mu[k]
                normal_scores[i][k] = np.exp(-0.5*(temp.dot(np.linalg.inv(self.cov[k])).dot(temp.T))) / (
                    np.sqrt(((2*np.pi)**D)*np.abs(np.linalg.det(self.cov[k])))) * self.w[k]
        return normal_scores

    def fit(self, X):
        # init
        self.N = X.shape[0]
        self.D = X.shape[1]
        self.w = np.abs(np.random.randn(self.n_components))
        self.w = self.w/np.sum(self.w)
        self.mu = np.abs(np.random.randn(self.n_components, self.D))
        self.cov = np.abs(np.random.randn(self.n_components, self.D, self.D))
        self.reg_cov = np.identity(self.D) * 1e-6
        for k in range(self.n_components):
            np.fill_diagonal(self.cov[k], 2)
            self.cov[k] += self.reg_cov
        prev = 1e9
        for it in range(self.max_iter):
            # E step
            weighted_normal_scores = self.get_weighted_normal_scores(X)
            pik = weighted_normal_scores /                 weighted_normal_scores.sum(axis=1).reshape(-1, 1)
            # M step
            for k in range(self.n_components):
                spk = np.sum(pik[:, k])
                mu_new = (pik[:, k].reshape(-1, 1)*X).sum(axis=0)/spk
                self.w[k] = spk/self.N
                self.cov[k] = (pik[:, k].reshape(-1, 1) *
                               (X-mu_new)).T.dot((X-mu_new)) / spk
                self.cov[k] += self.reg_cov
                self.mu[k] = mu_new
            # Evaluate
            error = -np.sum(np.log(weighted_normal_scores.sum(axis=1)))
            if prev - error > 1e-6:
                prev = error
            else:
                break
            print(f'Iteration {it}, Error: {error:.6f}')

    def predict(self, X):
        print(self.mu)
        print(self.cov)
        print(self.w)
        wieghted_normal_scores = self.get_weighted_normal_scores(X)
        pik = wieghted_normal_scores /             wieghted_normal_scores.sum(axis=1).reshape(-1, 1)
        return np.argmax(pik, axis=1)


# %%
gmm = GMM(3)
gmm.fit(principal_components)
result = gmm.predict(principal_components)


# %%
COLORS = ['b', 'r', 'g']
plt.title('2 Component PCA')
plt.xlabel('PC1')
plt.ylabel('PC2')
for k in range(3):
    temp = np.take(principal_components, np.where(result == k), axis=0)
    plt.scatter(temp[0][:, 0], temp[0][:, 1],
                edgecolors=COLORS[k], facecolors='none', label = f'{k+1}')
plt.gca().set_aspect('equal', adjustable='box')
plt.legend()
plt.savefig('gmm.png', dpi=500)
plt.show()


# %%



