import yfinance as yf

# Download 5 years of daily SPY data
spy = yf.download("SPY", start="2020-01-01", end="2026-01-01")

# clean the data
spy.columns = spy.columns.droplevel('Ticker')
spy.reset_index(inplace=True)

# Save it to a CSV file
spy.to_csv("historical_data.csv", index=False)