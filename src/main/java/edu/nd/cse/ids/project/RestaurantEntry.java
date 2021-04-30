package edu.nd.cse.ids.project;

import com.opencsv.bean.CsvBindByName;

public class RestaurantEntry
{
    @CsvBindByName
    private int RestaurantID;
    
    @CsvBindByName
    private String RestaurantName;

    @CsvBindByName
    private int CountryCode;
    
    @CsvBindByName
    private String  City;
    
    @CsvBindByName
		private String Address;
	
		@CsvBindByName
		private String Locality;

		@CsvBindByName
		private String LocalityVerbose;

	  @CsvBindByName
	  private float Longitude;
	     
	  @CsvBindByName
		private float Latitude;

		@CsvBindByName
		private String Cuisines;

		@CsvBindByName
		private float AverageCost; 

		@CsvBindByName
		private String Currency;
		
		@CsvBindByName
		private String CanBook;
	
		@CsvBindByName
		private String HasOnlineDelivery;
	
		@CsvBindByName
		private String DeliveryOpen;
	
		@CsvBindByName
		private String Switch; 
		
		@CsvBindByName
		private int PriceRange;
		
		@CsvBindByName
		private float Rating;
	
		@CsvBindByName
		private String RatingColor;
			
		@CsvBindByName
		private String RatingText;
			
		@CsvBindByName
		private int Votes;
		
		
		public int getRestaurantID()
    {
        return RestaurantID;
    }
    
    public String getRestaurantName()
    {
        return RestaurantName;
    }
    
    public int getCountryCode()
    {
        return CountryCode;
    }
    
    public String getCity()
    {
        return City;
    }

		public String getAddress()
		{
				return Address;
		}

		public String getLocality()
		{
				return Locality;
		}

		public String  getLocalityVerbose()
		{
				return LocalityVerbose;
		}

		public float getLongitude()
		{
				return Longitude;
		}

		public float getLatitude()
		{		
				return Latitude;
		}

		public String getCuisine()
		{
				return Cuisines;
		}

		public float getAverageCost()
		{
				return AverageCost;
		}

		public String getCurrency()
		{
				return Currency;
		}

		public String getCanBook()
		{
				return CanBook;
		}

		public String getHasOnlineDelivery()
		{
				return HasOnlineDelivery;
		}
		
		public String getDeliveryOpen()
		{
				return DeliveryOpen;
		}

		public String getSwitch()
		{
				return Switch;
		}

		public int getPriceRange()
		{
				return PriceRange;
		}

		public float getRating()
		{
				return Rating;
		}

		public String getRatingColor()
		{
				return RatingColor;
		}

		public String getRatingText()
		{
				return RatingText;
		}

		public int getVotes()
		{
				return Votes;
		}

}
