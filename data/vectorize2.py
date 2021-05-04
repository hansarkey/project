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
    df = pd.read_csv('/escnfs/home/hsarkey/cse40982/project/data/zomato_updated.csv', header=0, dtype={"RestaurantID":int, "RestaurantName": str, "CountryCode":int, "City":str, "Address":str, "Locality":str, "LocalityVerbose":str, "Longitude":float, "Latitude":float, "Cuisines":str, "AverageCost":int, "Currency":str, "CanBook":str, "HasOnlineDelivery":str, "DeliveryOpen":str, "Switch":str, "PriceRange":int, "Rating":float, "RatingColor":str, "RatingText":str, "Votes":int})

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

    for rid, city, cuisine, cost, book, delivery, rating, in zip(rids, cities, cuisines, costs, books, deliverys, ratings):
        #restaurantData[rid].append(rid)
        #restaurantData[rid].append(rname)
        restaurantData[rid].append(city)
        #restaurantData[rid].append(add)
        restaurantData[rid].append(cuisine)
        restaurantData[rid].append(cost)
        #restaurantData[rid].append(currency)
        restaurantData[rid].append(book)
        restaurantData[rid].append(delivery)
        #restaurantData[rid].append(priceRange)
        restaurantData[rid].append(rating)
        #restaurantData[rid].append(ratingText)
        #restaurantData[rid].append(vote)

    
    
    for key in restaurantData:
        dat.append(restaurantData[key])

    
    ndat = np.asarray(dat)


    # Thinking about how to read this in java. 
    # Decided best way likely to just read in by line, and then split by the "--" into array
    # commas don't work because of addresses
    with open('zomato_vectors2.txt', 'w') as f:
        for item in ndat:
            f.write("--".join(item))
            f.write("\n")
