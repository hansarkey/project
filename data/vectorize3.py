#simple script to make vectors out of our data

import csv
import re
import pandas as pd
from collections import defaultdict
from dateutil import parser
import numpy as np
from scipy import spatial


if __name__ == '__main__':
    # Read CSV
    df = pd.read_csv('zomato_updated.csv', header=0, dtype={"RestaurantID":int, "RestaurantName": str, "CountryCode":int, "City":str, "Address":str, "Locality":str, "LocalityVerbose":str, "Longitude":float, "Latitude":float, "Cuisines":str, "AverageCost":int, "Currency":str, "CanBook":str, "HasOnlineDelivery":str, "DeliveryOpen":str, "Switch":str, "PriceRange":int, "Rating":float, "RatingColor":str, "RatingText":str, "Votes":int})

    # read data into lists 
    rids = df.RestaurantID.tolist()
    rnames = df.RestaurantName.tolist()
    cities = df.City.tolist()
    adds = df.Address.tolist()
    cuisines = df.Cuisines.tolist()
    costs = df.AverageCost.tolist()
    currencys = df.Currency.tolist()
    books = df.CanBook.tolist()
    deliverys = df.HasOnlineDelivery.tolist()
    priceRanges = df.PriceRange.tolist()
    ratings = df.Rating.tolist()
    RatingTexts = df.RatingText.tolist()
    Votes = df.Votes.tolist()


    # use rids to index the restaurants for our zip
    allrids = set(rids)
    
    restaurantData = defaultdict(list)
    dat = []

    embeddings_index = {}
    with open('glove.6B.100d.txt') as f:
        for line in f:
            values = line.split()
            word = values[0]
            coefs = np.asarray(values[1:], dtype='float32')
            embeddings_index[word] = coefs

    cityDict = {}
    for word in cities:
        if word in cityDict:
            continue
        cityName = word.split(" ")
        if len(cityName) == 2:
            vector1 = embeddings_index.get(cityName[0].lower())
            vector2 = embeddings_index.get(cityName[1].lower())
            if vector1 is not None and vector2 is not None:
                embedding_vector = vector1 + vector2
                cityDict[word] = embedding_vector
        elif len(cityName) == 3:
            vector1 = embeddings_index.get(cityName[0].lower())
            vector2 = embeddings_index.get(cityName[1].lower())
            vector3 = embeddings_index.get(cityName[2].lower())
            if vector1 is not None and vector2 is not None and vector3 is not None:
                embedding_vector = vector1 + vector2 + vector3
                cityDict[word] = embedding_vector
        else:
            vector = embeddings_index.get(cityName[0].lower())
            if vector is not None:
                embedding_vector = vector
                cityDict[word] = embedding_vector


    for rid, city, cuisine, cost, book, delivery, rating, in zip(rids, cities, cuisines, costs, books, deliverys, ratings):
        if city not in cityDict.keys():
            continue
        restaurantData[rid].append(rid)
        #restaurantData[rid].append(rname)
        #restaurantData[rid].append(add)
        restaurantData[rid].append(cityDict[city])
        #Cuisine Embeddings
        cuisine_embeddings = np.asarray([0 for _ in range(100)], dtype='float32')
        #cuisine_embeddings = np.empty(100, dtype='float32')
        cuisineNames = str(cuisine).split(", ")
        firstCuisine = cuisineNames[0].split(" ")
        for i in range(0, len(firstCuisine)):
            c = embeddings_index.get(firstCuisine[i].lower(), "None")
            if c != "None":
                cuisine_embeddings += c
            else:
                cuisine_embeddings = "None"
                break
        '''if len(cuisineNames) > 1:
            for i in range(1, len(cuisineNames)):
                cuisine_embeddings += embeddings_index.get(cuisineNames[i].lower())'''
        restaurantData[rid].append(cuisine_embeddings)
        restaurantData[rid].append(float(cost/800000))
        #restaurantData[rid].append(currency)
        
        if book == "Yes":
            restaurantData[rid].append(1)
        else:
            restaurantData[rid].append(0)

        if delivery == "Yes":
            restaurantData[rid].append(1)
        else:
            restaurantData[rid].append(0)

        #restaurantData[rid].append(book)
        #restaurantData[rid].append(delivery)
        #restaurantData[rid].append(priceRange)
        restaurantData[rid].append(float(rating/5))
        restaurantData[rid].append(city)
        restaurantData[rid].append(cuisineNames[0])
        #restaurantData[rid].append(ratingText)
        #restaurantData[rid].append(vote)
        #print(cityDict[city])

    
    
    for key in restaurantData:
        dat.append(restaurantData[key])

    
    ndat = np.asarray(dat)


    # Thinking about how to read this in java. 
    # Decided best way likely to just read in by line, and then split by the "--" into array
    # commas don't work because of addresses
    with open('zomato_vectors3.txt', 'w') as f:
        for key in restaurantData.keys():
            # Some of the names of the cuisines are not in the glove embeddings
            if restaurantData[key][2] is "None":
                continue
            f.write(str(restaurantData[key][0]))
            f.write(",")
            for i in range(0, len(restaurantData[key][1])):
                f.write(str(restaurantData[key][1][i]))
                if i < len(restaurantData[key][1])-1:
                    f.write(" ")
            f.write(",")
            for i in range(0, len(restaurantData[key][2])):
                f.write(str(restaurantData[key][2][i]))
                if i < len(restaurantData[key][2])-1:
                    f.write(" ")
            f.write(",")
            f.write(str(restaurantData[key][3]))
            f.write(",")
            f.write(str(restaurantData[key][4]))
            f.write(",")
            f.write(str(restaurantData[key][5]))
            f.write(",")
            f.write(str(restaurantData[key][6]))
            f.write(",")
            f.write(str(restaurantData[key][7]))
            f.write(",")
            f.write(str(restaurantData[key][8]))
            f.write("\n")
    #pd.DataFrame(ndat).to_csv('zomato_vectors3.csv')
